package com.diabetes.bloodsugar.alarm;

import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.diabetes.bloodsugar.activity.DetailsAlarmActivity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class AlarmDetailsViewModel extends ViewModel {

    private MutableLiveData<LocalDateTime> alarmDateTime;
    private MutableLiveData<Integer> snoozeIntervalInMins;
    private MutableLiveData<Integer> snoozeFreq;
    private MutableLiveData<Integer> alarmType;
    private MutableLiveData<Integer> alarmVolume;
    private MutableLiveData<Boolean> isSnoozeOn;
    private MutableLiveData<Boolean> isRepeatOn;
    private MutableLiveData<Boolean> isChosenDateToday;
    private MutableLiveData<Uri> alarmToneUri;
    private MutableLiveData<LocalDate> minDate;
    private MutableLiveData<ArrayList<Integer>> repeatDays;
    private MutableLiveData<Integer> mode;
    private MutableLiveData<Integer> oldAlarmHour;
    private MutableLiveData<Integer> oldAlarmMinute;
    private MutableLiveData<Boolean> hasUserChosenDate;
    private MutableLiveData<String> alarmMessage;

    @SuppressWarnings("SimplifiableConditionalExpression")
    public boolean getHasUserChosenDate() {
        if (hasUserChosenDate == null) {
            hasUserChosenDate = new MutableLiveData<>(false);
        }
        return hasUserChosenDate.getValue() == null ? false : hasUserChosenDate.getValue();
    }

    public void setHasUserChosenDate(boolean hasUserChosenDate) {
        if (this.hasUserChosenDate == null) {
            this.hasUserChosenDate = new MutableLiveData<>();
        }
        this.hasUserChosenDate.setValue(hasUserChosenDate);
    }

    @NonNull
    public LocalDateTime getAlarmDateTime() {
        return Objects.requireNonNull(alarmDateTime.getValue(), "Alarm date-time was null.");
    }

    public void setAlarmDateTime(@NonNull LocalDateTime alarmDateTime) {
        if (this.alarmDateTime == null) {
            this.alarmDateTime = new MutableLiveData<>();
        }
        this.alarmDateTime.setValue(alarmDateTime);
    }

    public int getSnoozeIntervalInMins() {
        if (snoozeIntervalInMins == null) {
            snoozeIntervalInMins = new MutableLiveData<>(5);
        }
        return snoozeIntervalInMins.getValue() == null ? 5 : snoozeIntervalInMins.getValue();
    }

    public void setSnoozeIntervalInMins(int snoozeIntervalInMins) {
        if (this.snoozeIntervalInMins == null) {
            this.snoozeIntervalInMins = new MutableLiveData<>();
        }
        this.snoozeIntervalInMins.setValue(snoozeIntervalInMins);
    }

    public int getSnoozeFreq() {

        if (snoozeFreq == null) {
            snoozeFreq = new MutableLiveData<>(3);
        }
        return snoozeFreq.getValue() == null ? 3 : snoozeFreq.getValue();
    }

    public void setSnoozeFreq(int snoozeFreq) {
        if (this.snoozeFreq == null) {
            this.snoozeFreq = new MutableLiveData<>();
        }
        this.snoozeFreq.setValue(snoozeFreq);
    }

    public int getAlarmType() {
        if (alarmType == null) {
            alarmType = new MutableLiveData<>(ConstantsAndStatics.ALARM_TYPE_SOUND_ONLY);
        }
        return alarmType.getValue() == null ? ConstantsAndStatics.ALARM_TYPE_SOUND_ONLY : alarmType.getValue();

    }

    public void setAlarmType(int alarmType) {
        if (this.alarmType == null) {
            this.alarmType = new MutableLiveData<>();
        }
        this.alarmType.setValue(alarmType);
    }

    public int getAlarmVolume() {
        if (alarmVolume == null) {
            alarmVolume = new MutableLiveData<>(3);
        }
        return alarmVolume.getValue() == null ? 3 : alarmVolume.getValue();
    }

    public void setAlarmVolume(int alarmVolume) {
        if (this.alarmVolume == null) {
            this.alarmVolume = new MutableLiveData<>();
        }
        this.alarmVolume.setValue(alarmVolume);
    }

    @SuppressWarnings("SimplifiableConditionalExpression")
    public boolean getIsSnoozeOn() {
        if (isSnoozeOn == null) {
            isSnoozeOn = new MutableLiveData<>(true);
        }
        return isSnoozeOn.getValue() == null ? true : isSnoozeOn.getValue();

    }

    public void setIsSnoozeOn(boolean isSnoozeOn) {
        if (this.isSnoozeOn == null) {
            this.isSnoozeOn = new MutableLiveData<>();
        }
        this.isSnoozeOn.setValue(isSnoozeOn);
    }

    @SuppressWarnings("SimplifiableConditionalExpression")
    public boolean getIsRepeatOn() {
        if (isRepeatOn == null) {
            isRepeatOn = new MutableLiveData<>(false);
        }
        return isRepeatOn.getValue() == null ? false : isRepeatOn.getValue();

    }

    public void setIsRepeatOn(boolean isRepeatOn) {
        if (this.isRepeatOn == null) {
            this.isRepeatOn = new MutableLiveData<>();
        }
        this.isRepeatOn.setValue(isRepeatOn);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean getIsChosenDateToday() {
        if (isChosenDateToday == null) {
            isChosenDateToday = new MutableLiveData<>(getAlarmDateTime().toLocalDate().equals(LocalDate.now()));
        }
        return isChosenDateToday.getValue() == null ? getAlarmDateTime().toLocalDate().equals(LocalDate.now()) : isChosenDateToday.getValue();
    }

    public void setIsChosenDateToday(boolean isChosenDateToday) {
        if (this.isChosenDateToday == null) {
            this.isChosenDateToday = new MutableLiveData<>();
        }
        this.isChosenDateToday.setValue(isChosenDateToday);
    }

    @NonNull
    public Uri getAlarmToneUri() {
        if (alarmToneUri == null) {
            alarmToneUri = new MutableLiveData<>(Settings.System.DEFAULT_ALARM_ALERT_URI);
        }
        return alarmToneUri.getValue() == null ? Settings.System.DEFAULT_ALARM_ALERT_URI : alarmToneUri.getValue();
    }

    public void setAlarmToneUri(@NonNull Uri alarmToneUri) {
        if (this.alarmToneUri == null) {
            this.alarmToneUri = new MutableLiveData<>();
        }
        this.alarmToneUri.setValue(alarmToneUri);
    }

    @NonNull
    public LocalDate getMinDate() {
        return Objects.requireNonNull(minDate.getValue(), "Minimum date was null.");
    }

    public void setMinDate(@NonNull LocalDate minDate) {
        if (this.minDate == null) {
            this.minDate = new MutableLiveData<>();
        }
        this.minDate.setValue(minDate);
    }

    @Nullable
    public ArrayList<Integer> getRepeatDays() {
        return repeatDays.getValue();
    }

    public void setRepeatDays(@Nullable ArrayList<Integer> repeatDays) {
        if (this.repeatDays == null) {
            this.repeatDays = new MutableLiveData<>();
        }
        this.repeatDays.setValue(repeatDays);
    }

    public int getMode() {
        if (mode == null) {
            mode = new MutableLiveData<>(DetailsAlarmActivity.MODE_NEW_ALARM);
        }
        return mode.getValue() == null ? DetailsAlarmActivity.MODE_NEW_ALARM : mode.getValue();
    }

    public void setMode(int mode) {
        if (this.mode == null) {
            this.mode = new MutableLiveData<>();
        }
        this.mode.setValue(mode);
    }

    public int getOldAlarmHour() {
        if (oldAlarmHour == null || oldAlarmHour.getValue() == null) {
            throw new NullPointerException("Old alarm hour was null.");
        }
        return oldAlarmHour.getValue();
    }

    public void setOldAlarmHour(int oldAlarmHour) {
        if (this.oldAlarmHour == null) {
            this.oldAlarmHour = new MutableLiveData<>();
        }
        this.oldAlarmHour.setValue(oldAlarmHour);
    }

    public int getOldAlarmMinute() {
        if (oldAlarmMinute == null || oldAlarmMinute.getValue() == null) {
            throw new NullPointerException("Old alarm minute was null.");
        }
        return oldAlarmMinute.getValue();
    }

    public void setOldAlarmMinute(int oldAlarmMinute) {
        if (this.oldAlarmMinute == null) {
            this.oldAlarmMinute = new MutableLiveData<>();
        }
        this.oldAlarmMinute.setValue(oldAlarmMinute);
    }

    public LiveData<Integer> getLiveAlarmVolume() {
        return alarmVolume;
    }

    public LiveData<Boolean> getLiveIsRepeatOn() {
        return isRepeatOn;
    }

    public LiveData<Integer> getLiveAlarmType() {
        return alarmType;
    }

    @Nullable
    public String getAlarmMessage() {
        if (alarmMessage == null) {
            alarmMessage = new MutableLiveData<>(null);
        }
        return alarmMessage.getValue();
    }

    public void setAlarmMessage(@Nullable String alarmMessage) {
        if (this.alarmMessage == null) {
            this.alarmMessage = new MutableLiveData<>();
        }
        this.alarmMessage.setValue(alarmMessage);
    }
}
