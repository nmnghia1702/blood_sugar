package com.diabetes.bloodsugar.activity;

import static android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlarmManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.diabetes.bloodsugar.R;
import com.diabetes.bloodsugar.alarm.UpdateAlarmService;


public class RequestPermActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView permGiven;
    private boolean startedService;
    private static final String KEY_SERVICE_STARTED = "com.diabetes.bloodsugar.Act_ReqPerm.ServiceStarted";


    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_perm);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish();
        });

        if (savedInstanceState == null) {
            startedService = false;
        } else {
            startedService = savedInstanceState.getBoolean(KEY_SERVICE_STARTED);
        }

        permGiven = findViewById(R.id.permGivenTextView);
        permGiven.setVisibility(View.GONE);

        Button btnPermission = findViewById(R.id.btnPermission);
        btnPermission.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            startActivity(intent);
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_SERVICE_STARTED, startedService);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onResume() {
        super.onResume();
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager.canScheduleExactAlarms() && !startedService) {
            permGiven.setVisibility(View.VISIBLE);
            Intent intent = new Intent(this, UpdateAlarmService.class);
            ContextCompat.startForegroundService(this, intent);
        }
    }
}
