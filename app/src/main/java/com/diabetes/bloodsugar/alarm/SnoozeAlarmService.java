package com.diabetes.bloodsugar.alarm;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.diabetes.bloodsugar.R;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;


public class SnoozeAlarmService extends Service {

    public static int alarmID;
    private Bundle alarmDetails;
    private int numberOfTimesTheAlarmhasBeenSnoozed;
    private NotificationManager notificationManager;
    private CountDownTimer snoozeTimer;
    public static boolean isThisServiceRunning = false;
    private boolean preMatureDeath;
    private ArrayList<Integer> repeatDays;
    private PowerManager.WakeLock wakeLock;
    private int notifID;


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), ConstantsAndStatics.ACTION_CANCEL_ALARM)) {
                dismissAlarm();
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        alarmDetails = Objects.requireNonNull(Objects.requireNonNull(intent.getExtras()).getBundle(ConstantsAndStatics.BUNDLE_KEY_ALARM_DETAILS));
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notifID = UniqueNotifID.getID();
        startSelfForeground();
        preMatureDeath = true;
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "in.basulabs.shakealarmclock::AlarmSnoozeServiceWakelockTag");
        ConstantsAndStatics.cancelScheduledPeriodicWork(this);
        alarmID = alarmDetails.getInt(ConstantsAndStatics.BUNDLE_KEY_ALARM_ID);
        numberOfTimesTheAlarmhasBeenSnoozed = intent.getExtras().getInt(RingAlarmService.EXTRA_NO_OF_TIMES_SNOOZED);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstantsAndStatics.ACTION_CANCEL_ALARM);
        registerReceiver(broadcastReceiver, intentFilter);
        SnoozeAlarmService myInstance = this;
        ZonedDateTime alarmDateTime = ZonedDateTime.of(LocalDateTime.of(
                        alarmDetails.getInt(ConstantsAndStatics.BUNDLE_KEY_ALARM_YEAR),
                        alarmDetails.getInt(ConstantsAndStatics.BUNDLE_KEY_ALARM_MONTH),
                        alarmDetails.getInt(ConstantsAndStatics.BUNDLE_KEY_ALARM_DAY),
                        alarmDetails.getInt(ConstantsAndStatics.BUNDLE_KEY_ALARM_HOUR),
                        alarmDetails.getInt(ConstantsAndStatics.BUNDLE_KEY_ALARM_MINUTE), 0, 0),
                ZoneId.systemDefault());
        ZonedDateTime newAlarmDateTime = alarmDateTime.plusMinutes((long) numberOfTimesTheAlarmhasBeenSnoozed *
                alarmDetails.getInt(ConstantsAndStatics.BUNDLE_KEY_SNOOZE_TIME_IN_MINS));
        long millisInFuture = Math.abs(Duration.between(ZonedDateTime.now(), newAlarmDateTime).toMillis());
        wakeLock.acquire(millisInFuture + 5000);
        snoozeTimer = new CountDownTimer(millisInFuture, 60000) {
            @Override
            public void onTick(long millisUntilFinished) {
                myInstance.startSelfForeground();
            }

            @Override
            public void onFinish() {
                Intent intent1 = new Intent(myInstance, RingAlarmService.class)
                        .putExtra(ConstantsAndStatics.BUNDLE_KEY_ALARM_DETAILS, alarmDetails)
                        .putExtra(RingAlarmService.EXTRA_NO_OF_TIMES_SNOOZED, numberOfTimesTheAlarmhasBeenSnoozed);
                preMatureDeath = false;
                ContextCompat.startForegroundService(myInstance, intent1);
                myInstance.stopSelf();
            }
        };

        snoozeTimer.start();
        loadRepeatDays();
        return START_NOT_STICKY;
    }

    private void startSelfForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(notifID, buildSnoozeNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE);
        } else {
            startForeground(notifID, buildSnoozeNotification());
        }
        isThisServiceRunning = true;
    }

    private void loadRepeatDays() {
        if (alarmDetails.getBoolean(ConstantsAndStatics.BUNDLE_KEY_IS_REPEAT_ON)) {
            AlarmDatabase alarmDatabase = AlarmDatabase.getInstance(this);
            Thread thread = new Thread(() -> repeatDays = new ArrayList<>(alarmDatabase.alarmDAO()
                    .getAlarmRepeatDays(alarmDetails.getInt(ConstantsAndStatics.BUNDLE_KEY_ALARM_ID))));
            thread.start();
        } else {
            repeatDays = null;
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(Integer.toString(ConstantsAndStatics.NOTIF_CHANNEL_ID_SNOOZE),
                    getString(R.string.notif_channel_name_snoozed_alarms), importance);
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification buildSnoozeNotification() {
        createNotificationChannel();
        Intent intent = new Intent().setAction(ConstantsAndStatics.ACTION_CANCEL_ALARM);
        int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT : PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent contentPendingIntent = PendingIntent.getBroadcast(this, 5017, intent, flags);
        NotificationCompat.Action notifAction = new NotificationCompat.Action.Builder(R.drawable.ic_notif,
                getString(R.string.notifAction), contentPendingIntent).build();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Integer.toString(ConstantsAndStatics.NOTIF_CHANNEL_ID_SNOOZE))
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notifContent_snooze))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setSmallIcon(R.drawable.ic_notif)
                .setOnlyAlertOnce(true)
                .addAction(notifAction);
        String alarmMessage = alarmDetails.getString(ConstantsAndStatics.BUNDLE_KEY_ALARM_MESSAGE, null);
        if (alarmMessage != null) {
            builder.setContentTitle(getString(R.string.app_name))
                    .setContentText(alarmMessage)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(alarmMessage));
        }
        return builder.build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void dismissAlarm() {
        snoozeTimer.cancel();
        cancelPendingIntent();
        AlarmDatabase alarmDatabase = AlarmDatabase.getInstance(this);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Thread thread_toggleAlarm = new Thread(() ->
                alarmDatabase.alarmDAO().toggleAlarm(alarmDetails.getInt(ConstantsAndStatics.BUNDLE_KEY_ALARM_ID), 0));
        Intent intent = new Intent(getApplicationContext(), AlarmBroadcastReceiver.class)
                .setAction(ConstantsAndStatics.ACTION_DELIVER_ALARM)
                .setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_NO_CREATE : PendingIntent.FLAG_NO_CREATE;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                alarmDetails.getInt(ConstantsAndStatics.BUNDLE_KEY_ALARM_ID), intent, flags);
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
        }
        if (alarmDetails.getBoolean(ConstantsAndStatics.BUNDLE_KEY_IS_REPEAT_ON, false) && repeatDays != null && repeatDays.size() > 0) {
            LocalTime alarmTime = LocalTime.of(alarmDetails.getInt(ConstantsAndStatics.BUNDLE_KEY_ALARM_HOUR),
                    alarmDetails.getInt(ConstantsAndStatics.BUNDLE_KEY_ALARM_MINUTE));
            Collections.sort(repeatDays);

            LocalDateTime alarmDateTime = LocalDateTime.of(LocalDate.now(), alarmTime);
            int dayOfWeek = alarmDateTime.getDayOfWeek().getValue();
            for (int i = 0; i < repeatDays.size(); i++) {
                if (repeatDays.get(i) == dayOfWeek) {
                    if (alarmTime.isAfter(LocalTime.now())) {
                        // Alarm possible today, nothing more to do, break out of loop.
                        break;
                    }
                } else if (repeatDays.get(i) > dayOfWeek) {
                    // There is a day available in the same week for the alarm to ring; select that day and
                    // break from loop.
                    alarmDateTime = alarmDateTime.with(TemporalAdjusters.next(DayOfWeek.of(repeatDays.get(i))));
                    break;
                }
                if (i == repeatDays.size() - 1) {
                    // No day possible in this week. Select the first available date from next week.
                    alarmDateTime = alarmDateTime.with(TemporalAdjusters.next(DayOfWeek.of(repeatDays.get(0))));
                }
            }

            intent.putExtra(ConstantsAndStatics.BUNDLE_KEY_ALARM_DETAILS, alarmDetails);
            flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0;
            PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, alarmDetails.getInt(ConstantsAndStatics.BUNDLE_KEY_ALARM_ID),
                    intent, flags);
            ZonedDateTime zonedDateTime = ZonedDateTime.of(alarmDateTime.withSecond(0), ZoneId.systemDefault());
            alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(zonedDateTime.toEpochSecond() * 1000, pendingIntent2), pendingIntent2);
        } else {
            thread_toggleAlarm.start();
            try {
                thread_toggleAlarm.join();
            } catch (InterruptedException ignored) {
            }
        }
        ConstantsAndStatics.schedulePeriodicWork(this);
        preMatureDeath = false;
        stopForeground(true);
        stopSelf();

    }

    private void cancelPendingIntent() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmBroadcastReceiver.class)
                .setAction(ConstantsAndStatics.ACTION_DELIVER_ALARM)
                .setFlags(Intent.FLAG_RECEIVER_FOREGROUND)
                .putExtra(ConstantsAndStatics.BUNDLE_KEY_ALARM_DETAILS, alarmDetails);
        int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_NO_CREATE : PendingIntent.FLAG_NO_CREATE;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmID, intent, flags);
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (preMatureDeath) {
            dismissAlarm();
        }
        try {
            snoozeTimer.cancel();
        } catch (Exception ignored) {
        }
        wakeLock.release();
        unregisterReceiver(broadcastReceiver);
        isThisServiceRunning = false;
        alarmID = -1;
        notificationManager.cancel(notifID);
    }
}
