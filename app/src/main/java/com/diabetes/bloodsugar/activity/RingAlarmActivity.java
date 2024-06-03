package com.diabetes.bloodsugar.activity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.diabetes.bloodsugar.R;
import com.diabetes.bloodsugar.alarm.ConstantsAndStatics;

import java.time.LocalTime;
import java.util.Objects;


public class RingAlarmActivity extends AppCompatActivity implements View.OnClickListener {

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), ConstantsAndStatics.ACTION_DESTROY_RING_ALARM_ACTIVITY)) {
                finish();
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setTurnScreenOn(true);
            setShowWhenLocked(true);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring_alarm);

        TextView alarmTimeTextView = findViewById(R.id.alarmTimeTextView2);
        TextView alarmMessageTextView = findViewById(R.id.alarmmessageTextView);
        Button snoozeButton = findViewById(R.id.snoozeButton);
        ImageView cancelButton = findViewById(R.id.cancelButton);

        LocalTime localTime = LocalTime.now();

        if (DateFormat.is24HourFormat(this)) {
            alarmTimeTextView.setText(getResources().getString(R.string.time_24hour,
                    localTime.getHour(), localTime.getMinute()));
        } else {
            String amPm = localTime.getHour() < 12 ? "AM" : "PM";

            if ((localTime.getHour() <= 12) && (localTime.getHour() > 0)) {

                alarmTimeTextView.setText(getResources().getString(R.string.time_12hour,
                        localTime.getHour(), localTime.getMinute(), amPm));

            } else if (localTime.getHour() > 12 && localTime.getHour() <= 23) {

                alarmTimeTextView.setText(getResources().getString(R.string.time_12hour,
                        localTime.getHour() - 12, localTime.getMinute(), amPm));

            } else {
                alarmTimeTextView.setText(getResources().getString(R.string.time_12hour,
                        localTime.getHour() + 12, localTime.getMinute(), amPm));
            }
        }

        if (getIntent().getExtras() != null) {
            String message = getIntent().getExtras().getString(ConstantsAndStatics.BUNDLE_KEY_ALARM_MESSAGE, null);
            if (message != null) {
                int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
                if (screenSize == Configuration.SCREENLAYOUT_SIZE_SMALL) {
                    alarmMessageTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                }
            }
            alarmMessageTextView.setText(message != null ? message : getString(R.string.alarmMessage));
        }

        snoozeButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstantsAndStatics.ACTION_DESTROY_RING_ALARM_ACTIVITY);
        registerReceiver(broadcastReceiver, intentFilter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.snoozeButton) {
            Intent intent = new Intent(ConstantsAndStatics.ACTION_SNOOZE_ALARM);
            sendBroadcast(intent);
            finish();
        } else if (view.getId() == R.id.cancelButton) {
            Intent intent1 = new Intent(ConstantsAndStatics.ACTION_CANCEL_ALARM);
            sendBroadcast(intent1);
            finish();
        }
    }
}
