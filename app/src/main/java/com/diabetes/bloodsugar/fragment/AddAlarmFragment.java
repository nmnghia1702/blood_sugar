package com.diabetes.bloodsugar.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.diabetes.bloodsugar.R;
import com.diabetes.bloodsugar.activity.RingtonePickerActivity;
import com.diabetes.bloodsugar.alarm.ConstantsAndStatics;
import com.diabetes.bloodsugar.alarm.AlarmDetailsViewModel;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


public class AddAlarmFragment extends Fragment implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener, TimePicker.OnTimeChangedListener, AdapterView.OnItemSelectedListener {

    private static final int RINGTONE_REQUEST_CODE = 5280;
    private AlarmDetailsViewModel viewModel;
    private FragmentGUIListener listener;
    private TextView currentRepeatOptionsTV, currentSnoozeOptionsTV, alarmDateTV, alarmToneTV, alarmMessageTV;
    private ImageView alarmVolumeImageView;
    private boolean isSavedInstanceStateNull;

    public interface FragmentGUIListener {
        void onSaveButtonClick();

        void onRequestSnoozeFragCreation();

        void onRequestDatePickerFragCreation();

        void onRequestRepeatFragCreation();

        void onRequestMessageFragCreation();

        void onCancelButtonClick();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentGUIListener) {
            listener = (FragmentGUIListener) context;
        } else {
            throw new ClassCastException(context.getClass().getSimpleName() + " must implement AddAlarmFragment.FragmentGUIListener.");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isSavedInstanceStateNull = savedInstanceState == null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_alarm, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(AlarmDetailsViewModel.class);

        TimePicker timePicker = view.findViewById(R.id.addAlarmTimePicker);
        LinearLayoutCompat repeatConsLayout = view.findViewById(R.id.repeatConstraintLayout);
        LinearLayoutCompat snoozeConsLayout = view.findViewById(R.id.snoozeConstraintLayout);
        RelativeLayout alarmDateConstarintLayout = view.findViewById(R.id.alarmDateConstraintLayout);
        LinearLayoutCompat alarmToneConstraintLayout = view.findViewById(R.id.alarmToneConstraintLayout);
        LinearLayoutCompat alarmMessageConstraintLayout = view.findViewById(R.id.alarmMessageConstraintLayout);
        currentRepeatOptionsTV = view.findViewById(R.id.currentRepeatOptionsTextView);
        currentSnoozeOptionsTV = view.findViewById(R.id.currentSnoozeOptionTextView);
        Spinner alarmTypeSpinner = view.findViewById(R.id.alarmTypeSpinner);
        SeekBar alarmVolumeSeekbar = view.findViewById(R.id.alarmVolumeSeekbar);
        alarmVolumeImageView = view.findViewById(R.id.alarmVolumeImageView);
        TextView btnAddSave = view.findViewById(R.id.btnAddSave);
        TextView btnCancel = view.findViewById(R.id.btnCancel);
        alarmDateTV = view.findViewById(R.id.alarmDateTextView);
        alarmToneTV = view.findViewById(R.id.alarmToneTextView);
        TextView alarmDateLabel = view.findViewById(R.id.alarmDateLabel);
        TextView alarmVolumeLabel = view.findViewById(R.id.alarmVolumeLabel);
        TextView alarmToneLabel = view.findViewById(R.id.alarmToneLabel);
        alarmMessageTV = view.findViewById(R.id.textView_alarmMessage);

        timePicker.setIs24HourView(DateFormat.is24HourFormat(requireContext()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(viewModel.getAlarmDateTime().getHour());
            timePicker.setMinute(viewModel.getAlarmDateTime().getMinute());
        } else {
            timePicker.setCurrentHour(viewModel.getAlarmDateTime().getHour());
            timePicker.setCurrentMinute(viewModel.getAlarmDateTime().getMinute());
        }

        setDate();

        displayRepeatOptions();
        displaySnoozeOptions();
        displayAlarmTone();
        displayAlarmMessage();

        ArrayAdapter<CharSequence> alarmTypeAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.alarmTypeSpinnerEntries, android.R.layout.simple_spinner_item);
        alarmTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alarmTypeSpinner.setAdapter(alarmTypeAdapter);

        alarmTypeSpinner.setSelection(viewModel.getAlarmType());

        AudioManager audioManager = (AudioManager) requireContext().getSystemService(Context.AUDIO_SERVICE);
        alarmVolumeSeekbar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM));

        alarmVolumeSeekbar.setProgress(viewModel.getAlarmVolume());
        if (viewModel.getAlarmVolume() == 0) {
            alarmVolumeImageView.setImageResource(R.drawable.ic_volume_mute);
        } else {
            alarmVolumeImageView.setImageResource(R.drawable.ic_volume_high);
        }

        timePicker.setOnTimeChangedListener(this);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            try {
                ViewGroup amPmView;
                ViewGroup v1 = (ViewGroup) timePicker.getChildAt(0);
                ViewGroup v2 = (ViewGroup) v1.getChildAt(0);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    ViewGroup v3 = (ViewGroup) v2.getChildAt(0);
                    amPmView = (ViewGroup) v3.getChildAt(3);
                } else {
                    amPmView = (ViewGroup) v2.getChildAt(3);
                }
                View.OnClickListener listener = v -> timePicker.setCurrentHour((timePicker.getCurrentHour() + 12) % 24);

                View am = amPmView.getChildAt(0);
                View pm = amPmView.getChildAt(1);

                am.setOnClickListener(listener);
                pm.setOnClickListener(listener);
            } catch (Exception ignored) {
            }
        }

        btnAddSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        repeatConsLayout.setOnClickListener(this);
        snoozeConsLayout.setOnClickListener(this);
        alarmDateConstarintLayout.setOnClickListener(this);
        alarmToneConstraintLayout.setOnClickListener(this);
        alarmMessageConstraintLayout.setOnClickListener(this);
        alarmVolumeSeekbar.setOnSeekBarChangeListener(this);
        alarmTypeSpinner.setOnItemSelectedListener(this);
        viewModel.getLiveAlarmVolume().observe(getViewLifecycleOwner(), volume -> {
            if (volume == 0) {
                alarmVolumeImageView.setImageResource(R.drawable.ic_volume_mute);
            } else {
                alarmVolumeImageView.setImageResource(R.drawable.ic_volume_high);
            }
        });

        viewModel.getLiveIsRepeatOn().observe(getViewLifecycleOwner(), isRepeatOn -> {

            if (isRepeatOn) {

                alarmDateConstarintLayout.setEnabled(false);

                alarmDateLabel.setTextColor(getResources().getColor(R.color.white));
                alarmDateLabel.setPaintFlags(alarmDateLabel.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                alarmDateTV.setTextColor(getResources().getColor(R.color.white));
                alarmDateTV.setPaintFlags(alarmDateLabel.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            } else {

                alarmDateConstarintLayout.setEnabled(true);

                alarmDateLabel.setTextColor(getResources().getColor(R.color.black));
                alarmDateLabel.setPaintFlags(alarmDateLabel.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

                alarmDateTV.setTextColor(getResources().getColor(R.color.black));
                alarmDateTV.setPaintFlags(alarmDateLabel.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        });

        viewModel.getLiveAlarmType().observe(getViewLifecycleOwner(), alarmType -> {
            if (alarmType == ConstantsAndStatics.ALARM_TYPE_VIBRATE_ONLY) {
                alarmVolumeLabel.setTextColor(getResources().getColor(R.color.white));
                alarmVolumeLabel.setPaintFlags(alarmDateLabel.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                alarmVolumeSeekbar.setEnabled(false);
                alarmToneLabel.setTextColor(getResources().getColor(R.color.white));
                alarmToneLabel.setPaintFlags(alarmDateLabel.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                alarmToneTV.setTextColor(getResources().getColor(R.color.white));
                alarmToneTV.setPaintFlags(alarmDateLabel.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                alarmToneConstraintLayout.setEnabled(false);
            } else {
                alarmVolumeLabel.setTextColor(getResources().getColor(R.color.black));
                alarmVolumeLabel.setPaintFlags(alarmDateLabel.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                alarmVolumeSeekbar.setEnabled(true);
                alarmToneLabel.setTextColor(getResources().getColor(R.color.black));
                alarmToneLabel.setPaintFlags(alarmDateLabel.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                alarmToneTV.setTextColor(getResources().getColor(R.color.black));
                alarmToneTV.setPaintFlags(alarmDateLabel.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                alarmToneConstraintLayout.setEnabled(true);
            }
        });

        if (isSavedInstanceStateNull) {
            isSavedInstanceStateNull = false;
        }
        return view;
    }

    private void displayAlarmMessage() {
        if (viewModel.getAlarmMessage() == null) {
            alarmMessageTV.setText(R.string.alarmMessage_default);
        } else {
            alarmMessageTV.setText(viewModel.getAlarmMessage());
        }
    }

    private void displaySnoozeOptions() {
        if (viewModel.getIsSnoozeOn()) {
            currentSnoozeOptionsTV.setText(requireContext().getResources()
                    .getString(R.string.snoozeOptionsTV_snoozeOn,
                            viewModel.getSnoozeIntervalInMins(), viewModel.getSnoozeFreq()));
        } else {
            currentSnoozeOptionsTV.setText(requireContext().getResources().getString(R.string.snoozeOffLabel));
        }
    }

    private void displayRepeatOptions() {
        if (viewModel.getIsRepeatOn() && viewModel.getRepeatDays() != null) {
            viewModel.setIsRepeatOn(true);
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < viewModel.getRepeatDays().size(); i++) {
                int day = (viewModel.getRepeatDays().get(i) + 1) > 7 ? 1 : (viewModel.getRepeatDays().get(i) + 1);
                str.append(new DateFormatSymbols().getShortWeekdays()[day]);
                if (i < viewModel.getRepeatDays().size() - 1) {
                    str.append(", ");
                }
            }
            currentRepeatOptionsTV.setText(str.toString());
        } else {
            viewModel.setIsRepeatOn(false);
            currentRepeatOptionsTV.setText(requireContext().getResources().getString(R.string.repeatNone));
        }
    }

    private void displayAlarmTone() {
        if (viewModel.getAlarmToneUri().equals(Settings.System.DEFAULT_ALARM_ALERT_URI)) {
            alarmToneTV.setText(R.string.defaultAlarmToneText);
        } else {
            String fileName = null;

            try {
                try (Cursor cursor = requireContext().getContentResolver()
                        .query(viewModel.getAlarmToneUri(), null, null, null, null)) {

                    if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                        int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (index != -1) {
                            fileName = cursor.getString(index);
                        } else {
                            fileName = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
                        }
                    } else {
                        viewModel.setAlarmToneUri(Settings.System.DEFAULT_ALARM_ALERT_URI);
                        alarmToneTV.setText(R.string.defaultAlarmToneText);
                        return;
                    }
                }
            } catch (SecurityException se) {
                viewModel.setAlarmToneUri(Settings.System.DEFAULT_ALARM_ALERT_URI);
                alarmToneTV.setText(R.string.defaultAlarmToneText);
                return;
            } catch (Exception ignored) {
            }

            if (fileName != null) {
                alarmToneTV.setText(fileName);
            } else {
                alarmToneTV.setText(viewModel.getAlarmToneUri().getLastPathSegment());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setDate() {
        alarmDateTV.setText(viewModel.getAlarmDateTime().format(DateTimeFormatter.ofPattern("dd MMMM, yyyy")));
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btnAddSave) {
            saveButtonClicked();
        } else if (view.getId() == R.id.btnCancel) {
            listener.onCancelButtonClick();
        } else if (view.getId() == R.id.repeatConstraintLayout) {
            listener.onRequestRepeatFragCreation();
        } else if (view.getId() == R.id.snoozeConstraintLayout) {
            listener.onRequestSnoozeFragCreation();
        } else if (view.getId() == R.id.alarmDateConstraintLayout) {
            listener.onRequestDatePickerFragCreation();
        } else if (view.getId() == R.id.alarmToneConstraintLayout) {

            Intent intent = new Intent(requireContext(), RingtonePickerActivity.class)
                    .setAction(RingtoneManager.ACTION_RINGTONE_PICKER)
                    .putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
                    .putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select alarm tone:")
                    .putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
                    .putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                    .putExtra(ConstantsAndStatics.EXTRA_PLAY_RINGTONE, false)
                    .putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Settings.System.DEFAULT_ALARM_ALERT_URI)
                    .putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, viewModel.getAlarmToneUri());
            startActivityForResult(intent, RINGTONE_REQUEST_CODE);

        } else if (view.getId() == R.id.alarmMessageConstraintLayout) {
            listener.onRequestMessageFragCreation();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RINGTONE_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {

                assert data != null;
                Uri uri = Objects.requireNonNull(data.getExtras()).getParcelable(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                assert uri != null;
                viewModel.setAlarmToneUri(uri);
            }
        }
        displayAlarmTone();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
        viewModel.setAlarmDateTime(viewModel.getAlarmDateTime().withHour(hourOfDay));
        viewModel.setAlarmDateTime(viewModel.getAlarmDateTime().withMinute(minute));
        if (viewModel.getIsChosenDateToday()) {
            viewModel.setAlarmDateTime(LocalDateTime.of(LocalDate.now(), viewModel.getAlarmDateTime().toLocalTime()));
            if (!viewModel.getAlarmDateTime().toLocalTime().isAfter(LocalTime.now())) {
                //Date today NOT possible.
                viewModel.setAlarmDateTime(viewModel.getAlarmDateTime().plusDays(1));
                viewModel.setIsChosenDateToday(false);
                viewModel.setHasUserChosenDate(false);
            }

            viewModel.setMinDate(viewModel.getAlarmDateTime().toLocalDate());

        } else {
            if (!viewModel.getHasUserChosenDate()) {
                if (viewModel.getAlarmDateTime().toLocalTime().isAfter(LocalTime.now())) {
                    viewModel.setAlarmDateTime(LocalDateTime.of(LocalDate.now(),
                            viewModel.getAlarmDateTime().toLocalTime()));
                    viewModel.setIsChosenDateToday(true);
                }
            }

            if (!viewModel.getAlarmDateTime().toLocalTime().isAfter(LocalTime.now())) {
                viewModel.setMinDate(LocalDate.now().plusDays(1));
            } else {
                viewModel.setMinDate(LocalDate.now());
            }
        }
        setDate();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        viewModel.setAlarmVolume(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
        viewModel.setAlarmType(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    private void saveButtonClicked() {
        SharedPreferences sharedPreferences = requireContext()
                .getSharedPreferences(ConstantsAndStatics.SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE);

        if (sharedPreferences.getBoolean(ConstantsAndStatics.SHARED_PREF_KEY_AUTO_SET_TONE, true)) {
            sharedPreferences.edit()
                    .remove(ConstantsAndStatics.SHARED_PREF_KEY_DEFAULT_ALARM_TONE_URI)
                    .putString(ConstantsAndStatics.SHARED_PREF_KEY_DEFAULT_ALARM_TONE_URI,
                            viewModel.getAlarmToneUri().toString())
                    .commit();
        }
        listener.onSaveButtonClick();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
