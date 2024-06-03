package com.diabetes.bloodsugar.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.diabetes.bloodsugar.R;
import com.diabetes.bloodsugar.adapter.HistoryAdapter;
import com.diabetes.bloodsugar.db.DbSupport;
import com.telpoo.frame.object.BaseObject;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    ArrayList<BaseObject> listBloodSugar;
    private ImageView btnBack;
    RecyclerView rcViewHis;
    HistoryAdapter historyAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initView();
        initData();
    }

    public void initView() {
        btnBack = findViewById(R.id.btnBack);
        rcViewHis = findViewById(R.id.rcViewHis);
    }

    public void initData() {
        listBloodSugar = DbSupport.getListBloodSugar();
        rcViewHis.setHasFixedSize(true);
        rcViewHis.setItemAnimator(new DefaultItemAnimator());
        rcViewHis.setLayoutManager(new GridLayoutManager(this, 2));
        historyAdapter = new HistoryAdapter(this, listBloodSugar);
        rcViewHis.setAdapter(historyAdapter);
        historyAdapter.notifyDataSetChanged();

        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                initData();
            }
        }
    }
}
