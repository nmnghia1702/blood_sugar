package com.diabetes.bloodsugar.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Objects;

import static com.diabetes.bloodsugar.alarm.ConstantsAndStatics.ACTION_EXISTING_ALARM;
import static com.diabetes.bloodsugar.alarm.ConstantsAndStatics.ACTION_NEW_ALARM;
import static com.diabetes.bloodsugar.alarm.ConstantsAndStatics.ACTION_NEW_ALARM_FROM_INTENT;
import static com.diabetes.bloodsugar.alarm.ConstantsAndStatics.ALARM_TYPE_SOUND_ONLY;
import static com.diabetes.bloodsugar.alarm.ConstantsAndStatics.BUNDLE_KEY_ALARM_DAY;
import static com.diabetes.bloodsugar.alarm.ConstantsAndStatics.BUNDLE_KEY_ALARM_DETAILS;
import static com.diabetes.bloodsugar.alarm.ConstantsAndStatics.BUNDLE_KEY_ALARM_HOUR;
import static com.diabetes.bloodsugar.alarm.ConstantsAndStatics.BUNDLE_KEY_ALARM_MINUTE;
import static com.diabetes.bloodsugar.alarm.ConstantsAndStatics.BUNDLE_KEY_ALARM_MONTH;
import static com.diabetes.bloodsugar.alarm.ConstantsAndStatics.BUNDLE_KEY_ALARM_TONE_URI;
import static com.diabetes.bloodsugar.alarm.ConstantsAndStatics.BUNDLE_KEY_ALARM_TYPE;
import static com.diabetes.bloodsugar.alarm.ConstantsAndStatics.BUNDLE_KEY_ALARM_VOLUME;
import static com.diabetes.bloodsugar.alarm.ConstantsAndStatics.BUNDLE_KEY_ALARM_YEAR;
import static com.diabetes.bloodsugar.alarm.ConstantsAndStatics.BUNDLE_KEY_HAS_USER_CHOSEN_DATE;
import static com.diabetes.bloodsugar.alarm.ConstantsAndStatics.BUNDLE_KEY_IS_REPEAT_ON;
import static com.diabetes.bloodsugar.alarm.ConstantsAndStatics.BUNDLE_KEY_IS_SNOOZE_ON;
import static com.diabetes.bloodsugar.alarm.ConstantsAndStatics.BUNDLE_KEY_OLD_ALARM_HOUR;
import static com.diabetes.bloodsugar.alarm.ConstantsAndStatics.BUNDLE_KEY_OLD_ALARM_MINUTE;
import static com.diabetes.bloodsugar.alarm.ConstantsAndStatics.BUNDLE_KEY_REPEAT_DAYS;
import static com.diabetes.bloodsugar.alarm.ConstantsAndStatics.BUNDLE_KEY_SNOOZE_FREQUENCY;
import static com.diabetes.bloodsugar.alarm.ConstantsAndStatics.BUNDLE_KEY_SNOOZE_TIME_IN_MINS;
import static com.diabetes.bloodsugar.alarm.ConstantsAndStatics.SHARED_PREF_FILE_NAME;
import static com.diabetes.bloodsugar.alarm.ConstantsAndStatics.SHARED_PREF_KEY_DEFAULT_ALARM_TONE_URI;
import static com.diabetes.bloodsugar.alarm.ConstantsAndStatics.SHARED_PREF_KEY_DEFAULT_ALARM_VOLUME;
import static com.diabetes.bloodsugar.alarm.ConstantsAndStatics.SHARED_PREF_KEY_DEFAULT_SNOOZE_FREQ;
import static com.diabetes.bloodsugar.alarm.ConstantsAndStatics.SHARED_PREF_KEY_DEFAULT_SNOOZE_INTERVAL;
import static com.diabetes.bloodsugar.alarm.ConstantsAndStatics.SHARED_PREF_KEY_DEFAULT_SNOOZE_IS_ON;

import com.diabetes.bloodsugar.R;
import com.diabetes.bloodsugar.alarm.AlertDialogDiscardChanges;
import com.diabetes.bloodsugar.alarm.ConstantsAndStatics;
import com.diabetes.bloodsugar.alarm.AlarmDetailsViewModel;
import com.diabetes.bloodsugar.fragment.DatePickerAlarmFragment;
import com.diabetes.bloodsugar.fragment.AddAlarmFragment;
import com.diabetes.bloodsugar.fragment.MessageAlarmFragment;
import com.diabetes.bloodsugar.fragment.RepeatOptionsAlarmFragment;
import com.diabetes.bloodsugar.fragment.SnoozeOptionsAlarmFragment;


public class DetailsAlarmActivity extends AppCompatActivity implements AddAlarmFragment.FragmentGUIListener,
        AlertDialogDiscardChanges.DialogListener {

    private FragmentManager fragmentManager;
    private AlarmDetailsViewModel viewModel;
    private SharedPreferences sharedPreferences;

    private static final String BACK_STACK_TAG = "activityAlarmDetails_fragment_stack";

    private static final int FRAGMENT_MAIN = 100;
    private static final int FRAGMENT_SNOOZE = 103;
    private static final int FRAGMENT_REPEAT = 110;
    private static final int FRAGMENT_PICK_DATE = 203;
    private static final int FRAGMENT_ALARM_MESSAGE = 401;

    private static int whichFragment = 0;

    public static final int MODE_NEW_ALARM = 0, MODE_EXISTING_ALARM = 1;
    ImageView btnBack;
    TextView tvTitle;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_alarm);

        tvTitle = findViewById(R.id.tvTitle);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            if (fragmentManager.getBackStackEntryCount() > 1) {
                fragmentManager.popBackStackImmediate();
                whichFragment = FRAGMENT_MAIN;
                setActionBarTitle();
            } else {
                onCancelButtonClick();
            }
        });

        fragmentManager = getSupportFragmentManager();
        sharedPreferences = getSharedPreferences(SHARED_PREF_FILE_NAME, MODE_PRIVATE);
        viewModel = new ViewModelProvider(this).get(AlarmDetailsViewModel.class);
        if (savedInstanceState == null) {
            if (Objects.equals(getIntent().getAction(), ACTION_NEW_ALARM)) {
                setVariablesInViewModel();
                fragmentManager.beginTransaction()
                        .replace(R.id.addAlarmActFragHolder, new AddAlarmFragment())
                        .addToBackStack(BACK_STACK_TAG)
                        .commit();

            } else if (Objects.requireNonNull(getIntent().getAction()).equals(ACTION_EXISTING_ALARM)) {
                Bundle data = Objects.requireNonNull(getIntent().getExtras()).getBundle(BUNDLE_KEY_ALARM_DETAILS);
                assert data != null;
                setVariablesInViewModel(MODE_EXISTING_ALARM,
                        data.getInt(BUNDLE_KEY_ALARM_HOUR),
                        data.getInt(BUNDLE_KEY_ALARM_MINUTE),
                        data.getInt(BUNDLE_KEY_ALARM_DAY),
                        data.getInt(BUNDLE_KEY_ALARM_MONTH),
                        data.getInt(BUNDLE_KEY_ALARM_YEAR),
                        data.getBoolean(BUNDLE_KEY_IS_SNOOZE_ON),
                        data.getBoolean(BUNDLE_KEY_IS_REPEAT_ON),
                        data.getInt(BUNDLE_KEY_SNOOZE_FREQUENCY),
                        data.getInt(BUNDLE_KEY_SNOOZE_TIME_IN_MINS),
                        data.getInt(BUNDLE_KEY_ALARM_TYPE),
                        data.getInt(BUNDLE_KEY_ALARM_VOLUME),
                        data.getIntegerArrayList(BUNDLE_KEY_REPEAT_DAYS),
                        data.getString(ConstantsAndStatics.BUNDLE_KEY_ALARM_MESSAGE),
                        Objects.requireNonNull(data.getParcelable(BUNDLE_KEY_ALARM_TONE_URI)),
                        data.getBoolean(BUNDLE_KEY_HAS_USER_CHOSEN_DATE));
                fragmentManager.beginTransaction()
                        .replace(R.id.addAlarmActFragHolder, new AddAlarmFragment())
                        .addToBackStack(BACK_STACK_TAG)
                        .commit();
            } else if (getIntent().getAction().equals(ACTION_NEW_ALARM_FROM_INTENT)) {
                Bundle data = getIntent().getExtras();
                if (data == null) {
                    setVariablesInViewModel();
                } else {
                    setVariablesInViewModel(MODE_NEW_ALARM,
                            data.getInt(BUNDLE_KEY_ALARM_HOUR),
                            data.getInt(BUNDLE_KEY_ALARM_MINUTE),
                            data.getInt(BUNDLE_KEY_ALARM_DAY),
                            data.getInt(BUNDLE_KEY_ALARM_MONTH),
                            data.getInt(BUNDLE_KEY_ALARM_YEAR),
                            sharedPreferences.getBoolean(SHARED_PREF_KEY_DEFAULT_SNOOZE_IS_ON, true),
                            data.getBoolean(BUNDLE_KEY_IS_REPEAT_ON),
                            sharedPreferences.getInt(SHARED_PREF_KEY_DEFAULT_SNOOZE_FREQ, 3),
                            sharedPreferences.getInt(SHARED_PREF_KEY_DEFAULT_SNOOZE_INTERVAL, 5),
                            data.getInt(BUNDLE_KEY_ALARM_TYPE),
                            data.getInt(BUNDLE_KEY_ALARM_VOLUME),
                            data.getIntegerArrayList(BUNDLE_KEY_REPEAT_DAYS),
                            data.getString(ConstantsAndStatics.BUNDLE_KEY_ALARM_MESSAGE, null),
                            Objects.requireNonNull(data.getParcelable(BUNDLE_KEY_ALARM_TONE_URI)), false);
                }

                fragmentManager.beginTransaction()
                        .replace(R.id.addAlarmActFragHolder, new AddAlarmFragment())
                        .addToBackStack(BACK_STACK_TAG)
                        .commit();
            }

            fragmentManager.executePendingTransactions();
            whichFragment = FRAGMENT_MAIN;
        }
        setActionBarTitle();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setVariablesInViewModel() {
        viewModel.setMode(MODE_NEW_ALARM);
        viewModel.setAlarmDateTime(LocalDateTime.now().plusHours(1));
        viewModel.setIsSnoozeOn(sharedPreferences.getBoolean(SHARED_PREF_KEY_DEFAULT_SNOOZE_IS_ON, true));
        viewModel.setIsRepeatOn(false);
        String alarmTone = sharedPreferences.getString(SHARED_PREF_KEY_DEFAULT_ALARM_TONE_URI, null);
        viewModel.setAlarmToneUri(alarmTone != null ? Uri.parse(alarmTone) : Settings.System.DEFAULT_ALARM_ALERT_URI);
        viewModel.setAlarmType(ALARM_TYPE_SOUND_ONLY);
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        viewModel.setAlarmVolume(sharedPreferences.getInt(SHARED_PREF_KEY_DEFAULT_ALARM_VOLUME,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM) - 2));
        viewModel.setSnoozeFreq(sharedPreferences.getInt(SHARED_PREF_KEY_DEFAULT_SNOOZE_FREQ, 3));
        viewModel.setSnoozeIntervalInMins(sharedPreferences.getInt(SHARED_PREF_KEY_DEFAULT_SNOOZE_INTERVAL, 5));
        viewModel.setRepeatDays(null);
        viewModel.setIsChosenDateToday(viewModel.getAlarmDateTime().toLocalDate().equals(LocalDate.now()));

        if (viewModel.getIsChosenDateToday()) {
            viewModel.setMinDate(viewModel.getAlarmDateTime().toLocalDate());
        } else {
            if (!viewModel.getAlarmDateTime().toLocalTime().isAfter(LocalTime.now())) {
                viewModel.setMinDate(LocalDate.now().plusDays(1));
            } else {
                viewModel.setMinDate(LocalDate.now());
            }
        }

        viewModel.setAlarmMessage(null);
        viewModel.setHasUserChosenDate(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setVariablesInViewModel(int mode, int alarmHour, int alarmMinute, int dayOfMonth, int month, int year, boolean isSnoozeOn,
                                         boolean isRepeatOn, int snoozeFreq, int snoozeIntervalInMins, int alarmType, int alarmVolume,
                                         @Nullable ArrayList<Integer> repeatDays, @Nullable String alarmMessage,
                                         @NonNull Uri alarmToneUri, boolean hasUserChosenDate) {
        viewModel.setMode(mode);
        viewModel.setAlarmDateTime(LocalDateTime.of(year, month, dayOfMonth, alarmHour, alarmMinute));
        viewModel.setIsSnoozeOn(isSnoozeOn);
        viewModel.setIsRepeatOn(isRepeatOn);
        viewModel.setAlarmToneUri(alarmToneUri);
        viewModel.setAlarmType(alarmType);
        viewModel.setAlarmVolume(alarmVolume);

        if (isSnoozeOn) {
            viewModel.setSnoozeFreq(snoozeFreq);
            viewModel.setSnoozeIntervalInMins(snoozeIntervalInMins);
        } else {
            viewModel.setSnoozeFreq(sharedPreferences.getInt(SHARED_PREF_KEY_DEFAULT_SNOOZE_FREQ, 3));
            viewModel.setSnoozeIntervalInMins(sharedPreferences.getInt(SHARED_PREF_KEY_DEFAULT_SNOOZE_INTERVAL, 5));
        }

        if (isRepeatOn && repeatDays != null) {
            viewModel.setRepeatDays(repeatDays);
        } else {
            viewModel.setRepeatDays(null);
        }

        viewModel.setIsChosenDateToday(viewModel.getAlarmDateTime().toLocalDate().equals(LocalDate.now()));

        if (viewModel.getIsChosenDateToday()) {
            viewModel.setMinDate(viewModel.getAlarmDateTime().toLocalDate());
        } else {
            if (!viewModel.getAlarmDateTime().toLocalTime().isAfter(LocalTime.now())) {
                viewModel.setMinDate(LocalDate.now().plusDays(1));
            } else {
                viewModel.setMinDate(LocalDate.now());
            }
        }

        viewModel.setAlarmMessage(alarmMessage);
        viewModel.setHasUserChosenDate(hasUserChosenDate);

        if (mode == MODE_EXISTING_ALARM) {
            viewModel.setOldAlarmHour(alarmHour);
            viewModel.setOldAlarmMinute(alarmMinute);
        }
    }

    private void setActionBarTitle() {
        switch (whichFragment) {
            case FRAGMENT_MAIN:
                if (viewModel.getMode() == MODE_NEW_ALARM) {
                    tvTitle.setText(R.string.actionBarTitle_newAlarm);
                } else if (viewModel.getMode() == MODE_EXISTING_ALARM) {
                    tvTitle.setText(R.string.actionBarTitle_editAlarm);
                }
                break;
            case FRAGMENT_SNOOZE:
                tvTitle.setText(R.string.actionBarTitle_snoozeOptions);
                break;
            case FRAGMENT_REPEAT:
                tvTitle.setText(R.string.actionBarTitle_repeatOptions);
                break;
            case FRAGMENT_PICK_DATE:
                tvTitle.setText(R.string.actionBarTitle_dateOptions);
                break;
            case FRAGMENT_ALARM_MESSAGE:
                tvTitle.setText(R.string.actionBarTitle_alarmMessage);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStackImmediate();
            whichFragment = FRAGMENT_MAIN;
            setActionBarTitle();
        } else {
            onCancelButtonClick();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSaveButtonClick() {
        Bundle data = new Bundle();
        data.putInt(BUNDLE_KEY_ALARM_HOUR, viewModel.getAlarmDateTime().getHour());
        data.putInt(BUNDLE_KEY_ALARM_MINUTE, viewModel.getAlarmDateTime().getMinute());
        data.putInt(BUNDLE_KEY_ALARM_DAY, viewModel.getAlarmDateTime().getDayOfMonth());
        data.putInt(BUNDLE_KEY_ALARM_MONTH, viewModel.getAlarmDateTime().getMonthValue());
        data.putInt(BUNDLE_KEY_ALARM_YEAR, viewModel.getAlarmDateTime().getYear());
        data.putInt(BUNDLE_KEY_ALARM_TYPE, viewModel.getAlarmType());
        data.putBoolean(BUNDLE_KEY_IS_SNOOZE_ON, viewModel.getIsSnoozeOn());
        data.putBoolean(BUNDLE_KEY_IS_REPEAT_ON, viewModel.getIsRepeatOn());
        data.putInt(BUNDLE_KEY_ALARM_VOLUME, viewModel.getAlarmVolume());
        data.putInt(BUNDLE_KEY_SNOOZE_TIME_IN_MINS, viewModel.getSnoozeIntervalInMins());
        data.putInt(BUNDLE_KEY_SNOOZE_FREQUENCY, viewModel.getSnoozeFreq());
        data.putIntegerArrayList(BUNDLE_KEY_REPEAT_DAYS, viewModel.getRepeatDays());
        data.putParcelable(BUNDLE_KEY_ALARM_TONE_URI, viewModel.getAlarmToneUri());
        data.putString(ConstantsAndStatics.BUNDLE_KEY_ALARM_MESSAGE, viewModel.getAlarmMessage());

        if (viewModel.getIsRepeatOn()) {
            data.putBoolean(BUNDLE_KEY_HAS_USER_CHOSEN_DATE, false);
        } else {
            data.putBoolean(BUNDLE_KEY_HAS_USER_CHOSEN_DATE, viewModel.getHasUserChosenDate());
        }

        if (viewModel.getMode() == MODE_EXISTING_ALARM) {
            data.putInt(BUNDLE_KEY_OLD_ALARM_HOUR, viewModel.getOldAlarmHour());
            data.putInt(BUNDLE_KEY_OLD_ALARM_MINUTE, viewModel.getOldAlarmMinute());
        }

        Intent intent = new Intent().putExtra(BUNDLE_KEY_ALARM_DETAILS, data);
        setResult(RESULT_OK, intent);
        this.finish();
    }

    @Override
    public void onRequestSnoozeFragCreation() {
        whichFragment = FRAGMENT_SNOOZE;
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction()
                        .replace(R.id.addAlarmActFragHolder, new SnoozeOptionsAlarmFragment())
                        .addToBackStack(BACK_STACK_TAG);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
        setActionBarTitle();
    }

    @Override
    public void onRequestDatePickerFragCreation() {
        fragmentManager.beginTransaction()
                .replace(R.id.addAlarmActFragHolder, new DatePickerAlarmFragment())
                .addToBackStack(BACK_STACK_TAG)
                .commit();
        fragmentManager.executePendingTransactions();
        whichFragment = FRAGMENT_PICK_DATE;
        setActionBarTitle();
    }

    @Override
    public void onRequestRepeatFragCreation() {
        fragmentManager.beginTransaction()
                .replace(R.id.addAlarmActFragHolder, new RepeatOptionsAlarmFragment())
                .addToBackStack(BACK_STACK_TAG)
                .commit();
        fragmentManager.executePendingTransactions();
        whichFragment = FRAGMENT_REPEAT;
        setActionBarTitle();
    }

    @Override
    public void onRequestMessageFragCreation() {
        fragmentManager.beginTransaction()
                .replace(R.id.addAlarmActFragHolder, new MessageAlarmFragment())
                .addToBackStack(BACK_STACK_TAG)
                .commit();
        fragmentManager.executePendingTransactions();
        whichFragment = FRAGMENT_ALARM_MESSAGE;
        setActionBarTitle();
    }

    @Override
    public void onCancelButtonClick() {
        DialogFragment cancelDialog = new AlertDialogDiscardChanges();
        cancelDialog.setCancelable(false);
        cancelDialog.show(fragmentManager, "");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialogFragment) {
        if (dialogFragment.getClass() == AlertDialogDiscardChanges.class) {
            setResult(RESULT_CANCELED);
            this.finish();
        }
    }
}
