package com.diabetes.bloodsugar.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.diabetes.bloodsugar.R;
import com.diabetes.bloodsugar.action.Contact;


public class TagetRangeActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView btnBack;
    TextView tvTypeMl, tvLow, tvNormal, tvProDia, tvDia, tvLowBefore, tvNormalBefore, tvPreDiaBefore, tvDiaBefore, tvLowBeforeMeal, tvNormalBeforeMeal, tvPreDiaBeforeMeal,
            tvDiaBeforeMeal, tvLowFasting, tvNormalFasting, tvPreDiaFasting, tvDiaFasting, tvLowAfter1h, tvNormalAfter1h, tvProDiaAfter1h, tvDiaAfter1h, tvLowAfter2h,
            tvNormalAfter2h, tvProDiaAfter2h, tvDiaAfter2h, tvLowAfter, tvNormalAfter, tvProDiaAfter, tvDiaAfter, tvLowAsleep, tvNormalAsleep, tvProDiaAsleep, tvDiaAsleep;
    String type, typeEd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taget_range);

        initView();
        initData();
    }

    public void initView() {
        type = getIntent().getStringExtra("type");
        typeEd = getIntent().getStringExtra("typee");
        btnBack = findViewById(R.id.btnBack);
        tvTypeMl = findViewById(R.id.tvTypeMl);

        tvLow = findViewById(R.id.tvLow);
        tvNormal = findViewById(R.id.tvNormal);
        tvProDia = findViewById(R.id.tvProDia);
        tvDia = findViewById(R.id.tvDia);
        tvLowBefore = findViewById(R.id.tvLowBefore);
        tvNormalBefore = findViewById(R.id.tvNormalBefore);
        tvPreDiaBefore = findViewById(R.id.tvPreDiaBefore);
        tvDiaBefore = findViewById(R.id.tvDiaBefore);
        tvLowBeforeMeal = findViewById(R.id.tvLowBeforeMeal);
        tvNormalBeforeMeal = findViewById(R.id.tvNormalBeforeMeal);
        tvPreDiaBeforeMeal = findViewById(R.id.tvPreDiaBeforeMeal);
        tvDiaBeforeMeal = findViewById(R.id.tvDiaBeforeMeal);
        tvLowFasting = findViewById(R.id.tvLowFasting);
        tvNormalFasting = findViewById(R.id.tvNormalFasting);
        tvPreDiaFasting = findViewById(R.id.tvPreDiaFasting);
        tvDiaFasting = findViewById(R.id.tvDiaFasting);
        tvLowAfter1h = findViewById(R.id.tvLowAfter1h);
        tvNormalAfter1h = findViewById(R.id.tvNormalAfter1h);
        tvProDiaAfter1h = findViewById(R.id.tvProDiaAfter1h);
        tvDiaAfter1h = findViewById(R.id.tvDiaAfter1h);
        tvLowAfter2h = findViewById(R.id.tvLowAfter2h);
        tvNormalAfter2h = findViewById(R.id.tvNormalAfter2h);
        tvProDiaAfter2h = findViewById(R.id.tvProDiaAfter2h);
        tvDiaAfter2h = findViewById(R.id.tvDiaAfter2h);
        tvLowAfter = findViewById(R.id.tvLowAfter);
        tvNormalAfter = findViewById(R.id.tvNormalAfter);
        tvProDiaAfter = findViewById(R.id.tvProDiaAfter);
        tvDiaAfter = findViewById(R.id.tvDiaAfter);
        tvLowAsleep = findViewById(R.id.tvLowAsleep);
        tvNormalAsleep = findViewById(R.id.tvNormalAsleep);
        tvProDiaAsleep = findViewById(R.id.tvProDiaAsleep);
        tvDiaAsleep = findViewById(R.id.tvDiaAsleep);
    }

    public void initData() {
        if ((type.equals(Contact.MGDL)) || (typeEd.equals(Contact.MGDL))) {
            tvTypeMl.setText("unit:mg/dL");
            tvLow.setText("<72.0");
            tvNormal.setText("72.0~99.0");
            tvProDia.setText("99.0~126.0");
            tvDia.setText("≥126.0");
            tvLowBefore.setText("<72.0");
            tvNormalBefore.setText("72.0~99.0");
            tvPreDiaBefore.setText("99.0~126.0");
            tvDiaBefore.setText("≥126.0");
            tvLowBeforeMeal.setText("<72.0");
            tvNormalBeforeMeal.setText("72.0~99.0");
            tvPreDiaBeforeMeal.setText("99.0~126.0");
            tvDiaBeforeMeal.setText("≥126.0");
            tvLowFasting.setText("<72.0");
            tvNormalFasting.setText("72.0~99.0");
            tvPreDiaFasting.setText("99.0~126.0");
            tvDiaFasting.setText("≥126.0");
            tvLowAfter1h.setText("<72.0");
            tvNormalAfter1h.setText("72.0~140.0");
            tvProDiaAfter1h.setText("140.0~153.0");
            tvDiaAfter1h.setText("≥153.0");
            tvLowAfter2h.setText("<72.0");
            tvNormalAfter2h.setText("72.0~85.0");
            tvProDiaAfter2h.setText("85.0~126.0");
            tvDiaAfter2h.setText("≥126.0");
            tvLowAfter.setText("<72.0");
            tvNormalAfter.setText("72.0~99.0");
            tvProDiaAfter.setText("99.0~126.0");
            tvDiaAfter.setText("≥126.0");
            tvLowAsleep.setText("<72.0");
            tvNormalAsleep.setText("72.0~99.0");
            tvProDiaAsleep.setText("99.0~126.0");
            tvDiaAsleep.setText("≥126.0");
        } else {
            tvTypeMl.setText("unit:mmol/L");
            tvLow.setText("<4.0");
            tvNormal.setText("4.0~5.5");
            tvProDia.setText("5.5~7.0");
            tvDia.setText("≥7.0");
            tvLowBefore.setText("<4.0");
            tvNormalBefore.setText("4.0~5.5");
            tvPreDiaBefore.setText("5.5~7.0");
            tvDiaBefore.setText("≥7.0");
            tvLowBeforeMeal.setText("<4.0");
            tvNormalBeforeMeal.setText("4.0~5.5");
            tvPreDiaBeforeMeal.setText("5.5~7.0");
            tvDiaBeforeMeal.setText("≥7.0");
            tvLowFasting.setText("<4.0");
            tvNormalFasting.setText("4.0~5.5");
            tvPreDiaFasting.setText("5.5~7.0");
            tvDiaFasting.setText("≥7.0");
            tvLowAfter1h.setText("<4.0");
            tvNormalAfter1h.setText("4.0~7.7777777");
            tvProDiaAfter1h.setText("7.7777777~8.5");
            tvDiaAfter1h.setText("≥8.5");
            tvLowAfter2h.setText("<4.0");
            tvNormalAfter2h.setText("4.0~4.7222223");
            tvProDiaAfter2h.setText("4.7222223~7.0");
            tvDiaAfter2h.setText("≥7.0");
            tvLowAfter.setText("<4.0");
            tvNormalAfter.setText("4.0~5.5");
            tvProDiaAfter.setText("5.5~7.0");
            tvDiaAfter.setText("≥7.0");
            tvLowAsleep.setText("<4.0");
            tvNormalAsleep.setText("4.0~5.5");
            tvProDiaAsleep.setText("5.5~7.0");
            tvDiaAsleep.setText("≥7.0");
        }

        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
        }
    }
}
