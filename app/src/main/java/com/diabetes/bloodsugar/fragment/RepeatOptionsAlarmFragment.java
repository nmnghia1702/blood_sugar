package com.diabetes.bloodsugar.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.diabetes.bloodsugar.R;
import com.diabetes.bloodsugar.alarm.AlarmDetailsViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class RepeatOptionsAlarmFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private List<CheckBox> checkBoxArrayList;
    private AlarmDetailsViewModel viewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repeatoptions_alarm, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(AlarmDetailsViewModel.class);
        checkBoxArrayList = new ArrayList<>();
        checkBoxArrayList.add(view.findViewById(R.id.cbMon));
        checkBoxArrayList.add(view.findViewById(R.id.cbTue));
        checkBoxArrayList.add(view.findViewById(R.id.cbWed));
        checkBoxArrayList.add(view.findViewById(R.id.cbThu));
        checkBoxArrayList.add(view.findViewById(R.id.cbFri));
        checkBoxArrayList.add(view.findViewById(R.id.cbSat));
        checkBoxArrayList.add(view.findViewById(R.id.cbSun));

        for (CheckBox checkBox : checkBoxArrayList) {
            checkBox.setChecked(false);
        }

        if (viewModel.getRepeatDays() != null && viewModel.getRepeatDays().size() > 0) {
            for (int i : viewModel.getRepeatDays()) {
                checkBoxArrayList.get(i - 1).setChecked(true);
            }
        }

        for (CheckBox checkBox : checkBoxArrayList) {
            checkBox.setOnCheckedChangeListener(this);
        }
        return view;
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        CheckBox checkBox = (CheckBox) compoundButton;
        if (isChecked) {
            if (viewModel.getRepeatDays() == null) {
                viewModel.setRepeatDays(new ArrayList<>());
            }
            viewModel.getRepeatDays().add(checkBoxArrayList.indexOf(checkBox) + 1);
            viewModel.setIsRepeatOn(true);
        } else {
            int index = Objects.requireNonNull(viewModel.getRepeatDays(), "Repeat days array list was null!")
                    .indexOf(checkBoxArrayList.indexOf(checkBox) + 1);
            viewModel.getRepeatDays().remove(index);

            if (viewModel.getRepeatDays() != null && viewModel.getRepeatDays().size() == 0) {
                viewModel.setRepeatDays(null);
                viewModel.setIsRepeatOn(false);
            }
        }

        if (viewModel.getRepeatDays() != null) {
            viewModel.setIsRepeatOn(viewModel.getRepeatDays().size() > 0);
            Collections.sort(viewModel.getRepeatDays());
        }
    }
}
