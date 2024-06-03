package com.diabetes.bloodsugar.alarm;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.diabetes.bloodsugar.R;
import com.diabetes.bloodsugar.activity.RequestPermActivity;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;


public class UpdateAlarmService extends Service {

    private static final int NOTIFICATION_ID = 903;
    private AlarmDatabase alarmDatabase;
    public static boolean isThisServiceRunning = false;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, buildForegroundNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE);
        } else {
            startForeground(NOTIFICATION_ID, buildForegroundNotification());
        }
        isThisServiceRunning = true;

        ConstantsAndStatics.cancelScheduledPeriodicWork(this);

        alarmDatabase = AlarmDatabase.getInstance(this);

        ArrayList<AlarmEntity> alarmEntityArrayList = getActiveAlarms();

        if (alarmEntityArrayList != null && alarmEntityArrayList.size() > 0) {

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                displayErrorNoif();
            } else {
                cancelActiveAlarms(alarmEntityArrayList);
                activateAlarms(alarmEntityArrayList);
            }
        }

        stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isThisServiceRunning = false;
        ConstantsAndStatics.schedulePeriodicWork(this);
    }

    private void createForegroundNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(Integer.toString(ConstantsAndStatics.NOTIF_CHANNEL_ID_UPDATE),
                    getString(R.string.notif_channel_update), importance);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createErrorNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(Integer.toString(ConstantsAndStatics.NOTIF_CHANNEL_ID_ERROR),
                    getString(R.string.notif_channel_error), importance);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification buildForegroundNotification() {

        createForegroundNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Integer.toString(ConstantsAndStatics.NOTIF_CHANNEL_ID_UPDATE))
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(getResources().getString(R.string.updateAlarm_notifMessage))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .setSmallIcon(R.drawable.ic_notif);

        return builder.build();
    }

    @Nullable
    private ArrayList<AlarmEntity> getActiveAlarms() {

        AtomicReference<ArrayList<AlarmEntity>> alarmEntityArrayList = new AtomicReference<>(new ArrayList<>());

        Thread thread = new Thread(() -> alarmEntityArrayList.set(new ArrayList<>(alarmDatabase.alarmDAO().getActiveAlarms())));

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException ignored) {
        }

        return alarmEntityArrayList.get();

    }

    private void cancelActiveAlarms(@NonNull ArrayList<AlarmEntity> alarmEntityArrayList) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        for (AlarmEntity alarmEntity : alarmEntityArrayList) {

            ConstantsAndStatics.killServices(this, alarmEntity.alarmID);

            Intent intent = new Intent(UpdateAlarmService.this, AlarmBroadcastReceiver.class);
            intent.setAction(ConstantsAndStatics.ACTION_DELIVER_ALARM);
            intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);

            int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_NO_CREATE :
                    PendingIntent.FLAG_NO_CREATE;

            PendingIntent pendingIntent = PendingIntent.getBroadcast(UpdateAlarmService.this, alarmEntity.alarmID, intent, flags);

            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent);
            }

        }
    }

    @Nullable
    private ArrayList<Integer> getRepeatDays(int alarmID) {

        AtomicReference<ArrayList<Integer>> repeatDays = new AtomicReference<>();

        Thread thread = new Thread(() -> repeatDays.set(new ArrayList<>(alarmDatabase.alarmDAO().getAlarmRepeatDays(alarmID))));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException ignored) {
        }

        return repeatDays.get();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void activateAlarms(@NonNull ArrayList<AlarmEntity> alarmEntityArrayList) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        for (AlarmEntity alarmEntity : alarmEntityArrayList) {

            ArrayList<Integer> repeatDays = getRepeatDays(alarmEntity.alarmID);

            LocalDateTime alarmDateTime;

            LocalDate alarmDate = LocalDate.of(alarmEntity.alarmYear, alarmEntity.alarmMonth, alarmEntity.alarmDay);
            LocalTime alarmTime = LocalTime.of(alarmEntity.alarmHour, alarmEntity.alarmMinutes);

            if (alarmEntity.isRepeatOn && repeatDays != null && repeatDays.size() > 0) {

                // If repeat is ON, set the alarm as we normally would.

                Collections.sort(repeatDays);

                alarmDateTime = LocalDateTime.of(LocalDate.now(), alarmTime);
                int dayOfWeek = alarmDateTime.getDayOfWeek().getValue();

                for (int i = 0; i < repeatDays.size(); i++) {
                    if (repeatDays.get(i) == dayOfWeek) {
                        if (alarmTime.isAfter(LocalTime.now())) {
                            // Alarm possible today, nothing more to do, break out of loop.
                            break;
                        }
                    } else if (repeatDays.get(i) > dayOfWeek) {
                        alarmDateTime = alarmDateTime.with(TemporalAdjusters.next(DayOfWeek.of(repeatDays.get(i))));
                        break;
                    }
                    if (i == repeatDays.size() - 1) {
                        // No day possible in this week. Select the first available date from next week.
                        alarmDateTime = alarmDateTime.with(TemporalAdjusters.next(DayOfWeek.of(repeatDays.get(0))));
                    }
                }

            } else {

                // If repeat is OFF, first check whether the alarm time is reachable. If yes, then set the alarm, otherwise ignore this alarm and
                // switch it off in the database.

                alarmDateTime = LocalDateTime.of(alarmDate, alarmTime);

                if (!alarmDateTime.isAfter(LocalDateTime.now())) {
                    alarmDateTime = null;
                }

            }

            if (alarmDateTime != null) {

                Intent intent = new Intent(getApplicationContext(), AlarmBroadcastReceiver.class);
                intent.setAction(ConstantsAndStatics.ACTION_DELIVER_ALARM);
                intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);

                Bundle data = alarmEntity.getAlarmDetailsInABundle();
                data.putIntegerArrayList(ConstantsAndStatics.BUNDLE_KEY_REPEAT_DAYS, repeatDays);
                intent.putExtra(ConstantsAndStatics.BUNDLE_KEY_ALARM_DETAILS, data);

                int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0;

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmEntity.alarmID, intent, flags);

                ZonedDateTime zonedDateTime = ZonedDateTime.of(alarmDateTime, ZoneId.systemDefault());

                alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(zonedDateTime.toEpochSecond() * 1000, pendingIntent), pendingIntent);

            } else {

                Thread thread = new Thread(() -> alarmDatabase.alarmDAO().toggleAlarm(alarmEntity.alarmID, 0));

                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException ignored) {
                }

                postAlarmMissedNotification(alarmTime);

            }

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void postAlarmMissedNotification(LocalTime alarmTime) {

        createErrorNotificationChannel();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        DateTimeFormatter formatter;
        if (!DateFormat.is24HourFormat(this)) {
            formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault());
        } else {
            formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Integer.toString(ConstantsAndStatics.NOTIF_CHANNEL_ID_ERROR))
                .setContentTitle(getResources().getString(R.string.updateAlarm_alarmMissedTitle))
                .setContentText(getString(R.string.updateAlarm_alarmMissedText, alarmTime.format(formatter)))
                .setSmallIcon(R.drawable.ic_notif)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ERROR)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setOngoing(false);

        notificationManager.notify(UniqueNotifID.getID(), builder.build());

    }

    private void displayErrorNoif() {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        createErrorNotificationChannel();

        Intent intent = new Intent(getApplicationContext(), RequestPermActivity.class);

        int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT : PendingIntent.FLAG_UPDATE_CURRENT;

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 255, intent, flags);

        NotificationCompat.Action notifAction = new NotificationCompat.Action.Builder(R.drawable.ic_notif,
                getString(R.string.error_notif_body), pendingIntent).build();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Integer.toString(ConstantsAndStatics.NOTIF_CHANNEL_ID_ERROR))
                .setContentTitle(getString(R.string.error_notif_title))
                .setContentText(getString(R.string.error_notif_body))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ERROR)
                .setSmallIcon(R.drawable.ic_notif)
                .setOngoing(true)
                .setAutoCancel(true)
                .addAction(notifAction);

        notificationManager.notify(UniqueNotifID.getID(), builder.build());

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
