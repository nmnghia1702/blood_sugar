package com.diabetes.bloodsugar.alarm;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


public class AlarmsListViewModel extends ViewModel implements LifecycleObserver {

    private MutableLiveData<ArrayList<AlarmData>> alarmDataArrayList;
    private final MutableLiveData<Integer> alarmsCount = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> isAlarmPending = new MutableLiveData<>(false);
    private MutableLiveData<Bundle> pendingAlarmDetails;
    private final MutableLiveData<Boolean> isSettingsActOver = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> alreadyInitialized = new MutableLiveData<>(false);

    public LiveData<Integer> getLiveAlarmsCount() {
        return alarmsCount;
    }

    private void incrementAlarmsCount() {
        if (alarmsCount.getValue() == null) {
            alarmsCount.setValue(1);
        } else {
            alarmsCount.setValue(alarmsCount.getValue() + 1);
        }
    }

    private void decrementAlarmsCount() {
        if (alarmsCount.getValue() != null && alarmsCount.getValue() > 0) {
            alarmsCount.setValue(alarmsCount.getValue() - 1);
        }
    }

    public int getAlarmsCount(@NonNull AlarmDatabase alarmDatabase) {
        AtomicInteger count = new AtomicInteger(0);
        Thread thread = new Thread(() -> count.set(alarmDatabase.alarmDAO().getNumberOfAlarms()));
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException ignored) {
        }
        alarmsCount.setValue(count.get());
        return count.get();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init(@NonNull AlarmDatabase alarmDatabase, boolean wait) {
        if (alarmDataArrayList == null || alarmDataArrayList.getValue() == null || wait) {

            alarmDataArrayList = new MutableLiveData<>(new ArrayList<>());

            Thread thread = new Thread(() -> {
                List<AlarmEntity> alarmEntityList = alarmDatabase.alarmDAO().getAlarms();
                if (alarmEntityList != null) {
                    for (AlarmEntity entity : alarmEntityList) {
                        LocalDateTime alarmDateTime;
                        if (!entity.isRepeatOn && !entity.isAlarmOn) {
                            alarmDateTime = LocalDateTime.of(entity.alarmYear, entity.alarmMonth, entity.alarmDay, entity.alarmHour,
                                    entity.alarmMinutes);
                            if (alarmDateTime.isBefore(LocalDateTime.now())) {
                                while (alarmDateTime.isBefore(LocalDateTime.now())) {
                                    alarmDateTime = alarmDateTime.plusDays(1);
                                }
                                alarmDatabase.alarmDAO()
                                        .updateAlarmDate(entity.alarmHour, entity.alarmMinutes,
                                                alarmDateTime.getDayOfMonth(),
                                                alarmDateTime.getMonthValue(),
                                                alarmDateTime.getYear());
                                alarmDatabase.alarmDAO().toggleHasUserChosenDate(entity.alarmID, 0);
                            }
                        }
                    }

                    alarmEntityList = alarmDatabase.alarmDAO().getAlarms();
                    for (AlarmEntity entity : alarmEntityList) {
                        LocalDateTime alarmDateTime = LocalDateTime.of(entity.alarmYear, entity.alarmMonth,
                                entity.alarmDay, entity.alarmHour, entity.alarmMinutes);
                        ArrayList<Integer> repeatDays = entity.isRepeatOn ? new ArrayList<>(alarmDatabase.alarmDAO()
                                .getAlarmRepeatDays(entity.alarmID)) : null;
                        Objects.requireNonNull(alarmDataArrayList.getValue()).add(getAlarmDataObject(entity, alarmDateTime, repeatDays));
                    }

                    alarmsCount.postValue(alarmEntityList.size());
                    alreadyInitialized.postValue(true);
                }
            });

            thread.start();
            if (wait) {
                try {
                    thread.join();
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void initAndWait(@NonNull AlarmDatabase alarmDatabase) {
        if (alreadyInitialized.getValue() == null || !alreadyInitialized.getValue()) {
            init(alarmDatabase, true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void init(@NonNull AlarmDatabase alarmDatabase) {
        if (alreadyInitialized.getValue() == null || !alreadyInitialized.getValue()) {
            init(alarmDatabase, false);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void forceInitAndWait(@NonNull AlarmDatabase alarmDatabase) {
        init(alarmDatabase, true);
    }

    public ArrayList<AlarmData> getAlarmDataArrayList() {
        if (alarmDataArrayList.getValue() == null) {
            return new ArrayList<>();
        } else {
            return alarmDataArrayList.getValue();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int[] addAlarm(@NonNull AlarmDatabase alarmDatabase, @NonNull AlarmEntity alarmEntity, @Nullable ArrayList<Integer> repeatDays) {
        AtomicInteger alarmID = new AtomicInteger();
        Thread thread = new Thread(() -> {
            alarmDatabase.alarmDAO().addAlarm(alarmEntity);
            alarmID.set(alarmDatabase.alarmDAO().getAlarmId(alarmEntity.alarmHour, alarmEntity.alarmMinutes));
            if (alarmEntity.isRepeatOn && repeatDays != null) {
                Collections.sort(repeatDays);
                for (int day : repeatDays) {
                    alarmDatabase.alarmDAO().insertRepeatData(new RepeatEntity(alarmID.get(), day));
                }
            }
        });

        thread.start();
        LocalDateTime alarmDateTime = LocalDateTime.of(alarmEntity.alarmYear, alarmEntity.alarmMonth,
                alarmEntity.alarmDay, alarmEntity.alarmHour, alarmEntity.alarmMinutes);
        int scrollToPosition = 0;
        AlarmData newAlarmData = getAlarmDataObject(alarmEntity, alarmDateTime, repeatDays);
        if (alarmDataArrayList.getValue() == null || alarmDataArrayList.getValue().size() == 0) {
            alarmDataArrayList = new MutableLiveData<>(new ArrayList<>());
            Objects.requireNonNull(alarmDataArrayList.getValue()).add(newAlarmData);
        } else {
            int index = isAlarmInTheList(alarmEntity.alarmHour, alarmEntity.alarmMinutes);
            if (index != -1) {
                alarmDataArrayList.getValue().remove(index);
            }

            for (int i = 0; i < Objects.requireNonNull(alarmDataArrayList.getValue()).size(); i++) {
                if (alarmDataArrayList.getValue().get(i).getAlarmTime().isBefore(alarmDateTime.toLocalTime())) {
                    if ((i + 1) < alarmDataArrayList.getValue().size()) {
                        if (alarmDataArrayList.getValue().get(i + 1).getAlarmTime().isAfter(alarmDateTime.toLocalTime())) {
                            alarmDataArrayList.getValue().add(i + 1, newAlarmData);
                            scrollToPosition = i + 1;
                            break;
                        }
                    } else {
                        alarmDataArrayList.getValue().add(newAlarmData);
                        scrollToPosition = alarmDataArrayList.getValue().size() - 1;
                        break;
                    }
                }

                if (i == alarmDataArrayList.getValue().size() - 1) {
                    alarmDataArrayList.getValue().add(0, newAlarmData);
                    break;
                }
            }
        }

        try {
            thread.join();
        } catch (InterruptedException ignored) {
        }

        incrementAlarmsCount();
        return new int[]{alarmID.get(), scrollToPosition};
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private AlarmData getAlarmDataObject(@NonNull AlarmEntity entity, @NonNull LocalDateTime alarmDateTime,
                                         @Nullable ArrayList<Integer> repeatDays) {
        if (!entity.isRepeatOn) {
            return new AlarmData(entity.isAlarmOn, alarmDateTime, entity.alarmType, entity.alarmMessage);
        } else {
            assert repeatDays != null;
            return new AlarmData(entity.isAlarmOn, alarmDateTime.toLocalTime(), entity.alarmType, entity.alarmMessage, repeatDays);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int removeAlarm(@NonNull AlarmDatabase alarmDatabase, int hour, int mins) {
        int position = -1;
        AtomicInteger alarmId = new AtomicInteger();
        Thread thread = new Thread(() -> {
            alarmId.set(alarmDatabase.alarmDAO().getAlarmId(hour, mins));
            alarmDatabase.alarmDAO().deleteAlarm(hour, mins);
        });
        thread.start();

        for (int i = 0; i < Objects.requireNonNull(alarmDataArrayList.getValue()).size(); i++) {
            AlarmData alarmData = alarmDataArrayList.getValue().get(i);

            if (alarmData.getAlarmTime().equals(LocalTime.of(hour, mins))) {
                alarmDataArrayList.getValue().remove(i);
                position = i;
                break;
            }
        }

        try {
            thread.join();
        } catch (InterruptedException ignored) {
        }

        decrementAlarmsCount();
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int toggleAlarmState(@NonNull AlarmDatabase alarmDatabase, int hour, int mins, int newAlarmState) {
        AtomicInteger alarmId = new AtomicInteger();
        Thread thread = new Thread(() -> {
            alarmId.set(alarmDatabase.alarmDAO().getAlarmId(hour, mins));

            alarmDatabase.alarmDAO().toggleAlarm(alarmId.get(), newAlarmState);
        });
        thread.start();

        // Toggle the alarm status in the alarmDataArrayList:
        int index = isAlarmInTheList(hour, mins);
        AlarmData alarmData = Objects.requireNonNull(alarmDataArrayList.getValue()).get(index);
        alarmData.setSwitchedOn(newAlarmState == 1);
        alarmDataArrayList.getValue().set(index, alarmData);

        try {
            thread.join();
        } catch (InterruptedException ignored) {
        }
        return index;
    }

    public int getAlarmId(@NonNull AlarmDatabase alarmDatabase, int hour, int mins) {
        AtomicInteger alarmId = new AtomicInteger(0);
        Thread thread = new Thread(() -> {
            try {
                alarmId.set(alarmDatabase.alarmDAO().getAlarmId(hour, mins));
            } catch (Exception ex) {
                alarmId.set(0);
            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException ignored) {
        }
        return alarmId.get();
    }

    public ArrayList<Integer> getRepeatDays(@NonNull AlarmDatabase alarmDatabase, int hour, int mins) {
        AtomicReference<ArrayList<Integer>> repeatDays = new AtomicReference<>(new ArrayList<>());
        Thread thread = new Thread(() -> repeatDays.set(new ArrayList<>(alarmDatabase.alarmDAO()
                .getAlarmRepeatDays(getAlarmId(alarmDatabase, hour, mins)))));
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException ignored) {
        }
        return repeatDays.get();
    }

    public AlarmEntity getAlarmEntity(@NonNull AlarmDatabase alarmDatabase, int hour, int mins) {
        AtomicReference<AlarmEntity> alarmEntity = new AtomicReference<>();
        Thread thread = new Thread(() -> alarmEntity.set(alarmDatabase.alarmDAO().getAlarmDetails(hour, mins).get(0)));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException ignored) {
        }
        return alarmEntity.get();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private int isAlarmInTheList(int hour, int mins) {
        if (alarmDataArrayList.getValue() != null && alarmDataArrayList.getValue().size() > 0) {
            for (AlarmData alarmData : alarmDataArrayList.getValue()) {
                if (alarmData.getAlarmTime().equals(LocalTime.of(hour, mins))) {
                    return alarmDataArrayList.getValue().indexOf(alarmData);
                }
            }
        }
        return -1;
    }

    public boolean getPendingStatus() {
        return isAlarmPending.getValue() != null && isAlarmPending.getValue();
    }

    public void setPendingStatus(boolean status) {
        isAlarmPending.setValue(status);
    }

    public void savePendingAlarm(@Nullable Bundle data) {
        pendingAlarmDetails = new MutableLiveData<>();
        pendingAlarmDetails.setValue(data);
    }

    @Nullable
    public Bundle getPendingALarmData() {
        return pendingAlarmDetails.getValue();
    }

    public void setIsSettingsActOver(boolean isSettingsActOver) {
        this.isSettingsActOver.setValue(isSettingsActOver);
    }

    public boolean getIsSettingsActOver() {
        return Objects.requireNonNull(isSettingsActOver.getValue());
    }
}
