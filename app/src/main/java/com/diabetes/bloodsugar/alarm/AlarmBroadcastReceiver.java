package com.diabetes.bloodsugar.alarm;

import static android.content.Context.POWER_SERVICE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import androidx.core.content.ContextCompat;

import java.util.Objects;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		if (Objects.equals(intent.getAction(), ConstantsAndStatics.ACTION_DELIVER_ALARM)) {

			PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
			PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
					"com.diabetes.bloodsugar::AlarmRingServiceWakelockTag");
			wakeLock.acquire(60000);

			Intent intent1 = new Intent(context, RingAlarmService.class)
					.putExtra(ConstantsAndStatics.BUNDLE_KEY_ALARM_DETAILS,
					Objects.requireNonNull(intent.getExtras()).getBundle(ConstantsAndStatics.BUNDLE_KEY_ALARM_DETAILS));
			ContextCompat.startForegroundService(context, intent1);

		} else if (Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED)) {

			PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
			PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
					"com.diabetes.bloodsugar::AlarmUpdateServiceWakelockTag");
			wakeLock.acquire(60000);

			Intent intent1 = new Intent(context, UpdateAlarmService.class);
			ContextCompat.startForegroundService(context, intent1);
		}

	}
}
