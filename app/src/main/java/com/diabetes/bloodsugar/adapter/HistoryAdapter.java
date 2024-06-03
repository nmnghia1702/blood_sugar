package com.diabetes.bloodsugar.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diabetes.bloodsugar.R;
import com.diabetes.bloodsugar.action.Contact;
import com.diabetes.bloodsugar.activity.NewEditRecordActivity;
import com.diabetes.bloodsugar.bean.BloodSugarObj;
import com.telpoo.frame.object.BaseObject;

import java.util.ArrayList;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    ArrayList<BaseObject> listBloodSugar;
    BaseObject object = new BaseObject();
    private Activity activity;
    LayoutInflater inflater;

    public HistoryAdapter(Activity activity, ArrayList<BaseObject> listBloodSugar) {
        this.activity = activity;
        this.listBloodSugar = listBloodSugar;
        inflater = LayoutInflater.from(activity);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (listBloodSugar == null) return;
        object = listBloodSugar.get(position);

        holder.tvNameAdd.setText(object.get(BloodSugarObj.name));
        switch (object.get(BloodSugarObj.name)) {
            case "Low":
                holder.viewLove.setImageResource(R.drawable.ic_love_blue);
                break;

            case "Normal":
                holder.viewLove.setImageResource(R.drawable.ic_love_green);
                break;

            case "Pre-diabetes":
                holder.viewLove.setImageResource(R.drawable.ic_love_orange);
                break;

            case "Diabetes":
                holder.viewLove.setImageResource(R.drawable.ic_love_red);
                break;
        }

        holder.tvNumber.setText(object.get(BloodSugarObj.number).replace(".0", "") + ".0");
        holder.tvDate.setText(object.get(BloodSugarObj.date));
        holder.tvTime.setText(object.get(BloodSugarObj.time));
        holder.tvMgMl.setText(object.get(BloodSugarObj.mgdl));

        holder.btnEdit.setOnClickListener(v -> {
            startActivity(position);
        });
    }

    public void startActivity(int position) {
        Intent intent = new Intent(activity, NewEditRecordActivity.class);
        intent.putExtra("data", getItem(position));
        Log.d("ThaoTm", "object: " + getItem(position).toJson());
        intent.putExtra("type", Contact.EDITRECORD);
        activity.startActivityForResult(intent, 1);
    }

    public BaseObject getItem(int position) {
        return listBloodSugar.get(position);
    }

    @Override
    public int getItemCount() {
        return listBloodSugar.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView btnEdit, viewLove;
        private TextView tvNameAdd, tvNumber, tvDate, tvTime, tvMgMl;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            viewLove = itemView.findViewById(R.id.viewLove);
            tvNameAdd = itemView.findViewById(R.id.tvNameAdd);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvMgMl = itemView.findViewById(R.id.tvMgMl);
        }
    }
}
