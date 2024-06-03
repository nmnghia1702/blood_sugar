package com.diabetes.bloodsugar.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.diabetes.bloodsugar.R;
import com.diabetes.bloodsugar.action.Listener;
import com.diabetes.bloodsugar.action.SPRDialog;
import com.diabetes.bloodsugar.alarm.AlarmData;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Objects;


public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    private final AlarmAdapter.AdapterInterface listener;
    private final ArrayList<AlarmData> alarmDataArrayList;
    private Context context;
    private Activity activity;

    public AlarmAdapter(@NonNull ArrayList<AlarmData> alarmDataArrayList, @NonNull AlarmAdapter.AdapterInterface listener,
                        @NonNull Context context, Activity activity) {
        this.alarmDataArrayList = alarmDataArrayList;
        this.listener = listener;
        this.context = context;
        this.activity = activity;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
        return new ViewHolder(listItem);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        AlarmData alarmData = alarmDataArrayList.get(position);
        if (alarmData.isSwitchedOn()) {
            holder.btnCheckOn.setImageResource(R.drawable.eu);
        } else {
            holder.btnCheckOn.setImageResource(R.drawable.et);
        }

        if (DateFormat.is24HourFormat(context)) {
            holder.tvTime.setText(context.getResources().getString(R.string.time_24hour,
                    alarmData.getAlarmTime().getHour(),
                    alarmData.getAlarmTime().getMinute()));
        } else {
            String amPm = alarmData.getAlarmTime().getHour() < 12 ? "AM" : "PM";
            int alarmHour;
            if ((alarmData.getAlarmTime().getHour() > 0) && (alarmData.getAlarmTime().getHour() <= 12)) {
                alarmHour = alarmData.getAlarmTime().getHour();
            } else if (alarmData.getAlarmTime().getHour() > 12 && alarmData.getAlarmTime().getHour() <= 23) {
                alarmHour = alarmData.getAlarmTime().getHour() - 12;
            } else {
                alarmHour = alarmData.getAlarmTime().getHour() + 12;
            }
            holder.tvTime.setText(context.getResources().getString(R.string.time_12hour, alarmHour,
                    alarmData.getAlarmTime().getMinute(), amPm));
        }

        if (!alarmData.isRepeatOn()) {
            String nullMessage = "AlarmAdapter: alarmDateTime was null for a non-repetitive alarm.";
            int day = (Objects.requireNonNull(alarmData.getAlarmDateTime(), nullMessage).getDayOfWeek().getValue() + 1) > 7 ? 1 :
                    (alarmData.getAlarmDateTime().getDayOfWeek().getValue() + 1);
            holder.tvDate.setText(context.getResources().getString(R.string.date,
                    new DateFormatSymbols().getShortWeekdays()[day],
                    alarmData.getAlarmDateTime().getDayOfMonth(),
                    new DateFormatSymbols().getShortMonths()[alarmData.getAlarmDateTime().getMonthValue() - 1]));
        } else {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < Objects.requireNonNull(alarmData.getRepeatDays(), "AlarmAdapter: repeatDays was null.").size(); i++) {
                int day = (alarmData.getRepeatDays().get(i) + 1) > 7 ? 1 : (alarmData.getRepeatDays().get(i) + 1);
                str.append(new DateFormatSymbols().getShortWeekdays()[day].substring(0, 3));
                if (i < alarmData.getRepeatDays().size() - 1) {
                    str.append(" ");
                }
            }
            holder.tvDate.setText(str.toString());
        }

        holder.btnCheckOn.setOnClickListener(view -> {
            int newAlarmState;
            if (!alarmData.isSwitchedOn()) {
                newAlarmState = 1;
            } else {
                newAlarmState = 0;
            }
            listener.onOnOffButtonClick(holder.getLayoutPosition(),
                    alarmData.getAlarmTime().getHour(), alarmData.getAlarmTime().getMinute(), newAlarmState);
        });

        holder.btnDelete.setOnClickListener(view ->
                SPRDialog.showDialogYesNo(activity, context.getString(R.string.type_delete), context.getString(R.string.yes), context.getString(R.string.no), new Listener.OnDialogYesNoListener() {
                    @Override
                    public void onYesClick() {
                        listener.onDeleteButtonClicked(holder.getLayoutPosition(),
                                alarmData.getAlarmTime().getHour(), alarmData.getAlarmTime().getMinute());
                    }

                    @Override
                    public void onNoClick() {

                    }
                }));

        holder.btnEdit.setOnClickListener(view -> listener.onItemClicked(holder.getLayoutPosition(),
                alarmData.getAlarmTime().getHour(), alarmData.getAlarmTime().getMinute()));
    }

    @Override
    public int getItemCount() {
        return alarmDataArrayList.size();
    }

    public interface AdapterInterface {
        void onOnOffButtonClick(int rowNumber, int hour, int mins, int newAlarmState);

        void onDeleteButtonClicked(int rowNumber, int hour, int mins);

        void onItemClicked(int rowNumber, int hour, int mins);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView btnCheckOn, btnDelete, btnEdit;
        public TextView tvTime, tvDate;

        public ViewHolder(View view) {
            super(view);
            btnEdit = view.findViewById(R.id.btnEdit);
            btnCheckOn = view.findViewById(R.id.btnCheckOn);
            tvTime = view.findViewById(R.id.tvTime);
            tvDate = view.findViewById(R.id.tvDate);
            btnDelete = view.findViewById(R.id.btnDelete);
        }
    }
}
