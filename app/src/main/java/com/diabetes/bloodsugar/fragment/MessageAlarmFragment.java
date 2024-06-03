package com.diabetes.bloodsugar.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.diabetes.bloodsugar.R;
import com.diabetes.bloodsugar.alarm.AlarmDetailsViewModel;


public class MessageAlarmFragment extends Fragment {

    private AlarmDetailsViewModel viewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_alarm, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(AlarmDetailsViewModel.class);

        EditText edtMessage = view.findViewById(R.id.edtMessage);
        if (viewModel.getAlarmMessage() == null) {
            edtMessage.setText("");
        } else {
            edtMessage.setText(viewModel.getAlarmMessage());
        }

        edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() == 0) {
                    viewModel.setAlarmMessage(null);
                } else {
                    viewModel.setAlarmMessage(s == null ? null : s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        return view;
    }
}
