package com.diabetes.bloodsugar.alarm;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;


public class AlarmData {

    private boolean isSwitchedOn;
    private LocalDateTime alarmDateTime;
    private LocalTime alarmTime;
    private int alarmType;
    private boolean isRepeatOn;
    private ArrayList<Integer> repeatDays;
    private String alarmMessage;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public AlarmData(boolean isSwitchedOn, @NonNull LocalDateTime alarmDateTime, int alarmType, @Nullable String alarmMessage) {
        this.isSwitchedOn = isSwitchedOn;
        this.alarmDateTime = alarmDateTime;
        this.alarmType = alarmType;
        this.isRepeatOn = false;
        this.repeatDays = null;
        this.alarmTime = alarmDateTime.toLocalTime();
        this.alarmMessage = alarmMessage;
    }

    public AlarmData(boolean isSwitchedOn, @NonNull LocalTime alarmTime, int alarmType, @Nullable String alarmMessage,
                     @NonNull ArrayList<Integer> repeatDays) {
        this.isSwitchedOn = isSwitchedOn;
        this.alarmTime = alarmTime;
        this.alarmType = alarmType;
        this.isRepeatOn = true;
        this.repeatDays = repeatDays;
        this.alarmMessage = alarmMessage;
        this.alarmDateTime = null;
    }

    public boolean isRepeatOn() {
        return isRepeatOn;
    }

    public void setRepeatOn(boolean repeatOn) {
        isRepeatOn = repeatOn;
    }

    @Nullable
    public ArrayList<Integer> getRepeatDays() {
        return repeatDays;
    }

    public void setRepeatDays(@Nullable ArrayList<Integer> repeatDays) {
        this.repeatDays = repeatDays;
    }

    public boolean isSwitchedOn() {
        return isSwitchedOn;
    }

    public void setSwitchedOn(boolean switchedOn) {
        isSwitchedOn = switchedOn;
    }

    @Nullable
    public LocalDateTime getAlarmDateTime() {
        return alarmDateTime;
    }

    public void setAlarmDateTime(@Nullable LocalDateTime alarmDateTime) {
        this.alarmDateTime = alarmDateTime;
    }

    @NonNull
    public LocalTime getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(@NonNull LocalTime alarmTime) {
        this.alarmTime = alarmTime;
    }

    public int getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(int alarmType) {
        this.alarmType = alarmType;
    }

    public void setAlarmMessage(@Nullable String alarmMessage) {
        this.alarmMessage = alarmMessage;
    }

    @Nullable
    public String getAlarmMessage() {
        return alarmMessage;
    }
}
