package com.diabetes.bloodsugar.alarm;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.diabetes.bloodsugar.R;
import com.diabetes.bloodsugar.activity.RequestPermActivity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class WorkerAlarmsActivate extends Worker {

    Context context;
    private boolean stopExecuting;


    public WorkerAlarmsActivate(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Result doWork() {

        stopExecuting = false;

        if (RingAlarmService.isThisServiceRunning || SnoozeAlarmService.isThisServiceRunning) {
            return Result.failure();
        } else {
            return activateAlarmsIfInactive();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Result activateAlarmsIfInactive() {

        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final AlarmDatabase alarmDatabase = AlarmDatabase.getInstance(context);

        List<AlarmEntity> list = alarmDatabase.alarmDAO().getActiveAlarms();

        if (list != null && list.size() > 0) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    displayErrorNoif();
                    return Result.failure();
                }
            }

            for (AlarmEntity alarmEntity : list) {

                AtomicReference<ArrayList<Integer>> repeatDaysAtomic = new AtomicReference<>();

                alarmDatabase.alarmDAO().getAlarmRepeatDays(alarmEntity.alarmID);

                ArrayList<Integer> repeatDays = repeatDaysAtomic.get();

                Intent intent = new Intent(context.getApplicationContext(), AlarmBroadcastReceiver.class);
                intent.setAction(ConstantsAndStatics.ACTION_DELIVER_ALARM);
                intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);

                Bundle data = alarmEntity.getAlarmDetailsInABundle();
                data.putIntegerArrayList(ConstantsAndStatics.BUNDLE_KEY_REPEAT_DAYS, repeatDays);
                intent.putExtra(ConstantsAndStatics.BUNDLE_KEY_ALARM_DETAILS, data);

                int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
                        : PendingIntent.FLAG_NO_CREATE;

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), alarmEntity.alarmID, intent, flags);

                if (pendingIntent == null) {

                    LocalDateTime alarmDateTime = ConstantsAndStatics.getAlarmDateTime(LocalDate.of(alarmEntity.alarmYear, alarmEntity.alarmMonth,
                                    alarmEntity.alarmDay), LocalTime.of(alarmEntity.alarmHour, alarmEntity.alarmMinutes), alarmEntity.isRepeatOn,
                            repeatDays);

                    ZonedDateTime zonedDateTime = ZonedDateTime.of(alarmDateTime, ZoneId.systemDefault());

                    int flags2 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0;

                    PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context.getApplicationContext(), alarmEntity.alarmID, intent, flags2);

                    alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(zonedDateTime.toEpochSecond() * 1000, pendingIntent1),
                            pendingIntent1);

                }

                if ((stopExecuting && !isStopped()) || RingAlarmService.isThisServiceRunning || SnoozeAlarmService.isThisServiceRunning) {
                    return Result.failure();
                }
            }
        }
        return Result.success();
    }

    private void displayErrorNoif() {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(Integer.toString(ConstantsAndStatics.NOTIF_CHANNEL_ID_ERROR),
                    context.getString(R.string.notif_channel_error), importance);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(context.getApplicationContext(), RequestPermActivity.class);

        int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT : PendingIntent.FLAG_UPDATE_CURRENT;

        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 255, intent, flags);

        NotificationCompat.Action notifAction = new NotificationCompat.Action.Builder(R.drawable.ic_notif,
                context.getString(R.string.error_notif_body), pendingIntent).build();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Integer.toString(ConstantsAndStatics.NOTIF_CHANNEL_ID_ERROR))
                .setContentTitle(context.getString(R.string.error_notif_title))
                .setContentText(context.getString(R.string.error_notif_body))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ERROR)
                .setSmallIcon(R.drawable.ic_notif)
                .setOngoing(true)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .addAction(notifAction);

        notificationManager.notify(UniqueNotifID.getID(), builder.build());
    }

    @Override
    public void onStopped() {
        super.onStopped();
        stopExecuting = true;
    }
}
