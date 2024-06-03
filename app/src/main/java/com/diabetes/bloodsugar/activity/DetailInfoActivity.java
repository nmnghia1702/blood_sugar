package com.diabetes.bloodsugar.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.diabetes.bloodsugar.R;
import com.diabetes.bloodsugar.action.Contact;


public class DetailInfoActivity extends AppCompatActivity {

    ImageView btnBackDetail, imgDetail;
    TextView tvDetail, tvContent;
    String type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_info);

        initView();
        initData();
    }

    private void initView() {
        btnBackDetail = findViewById(R.id.btnBackDetail);
        imgDetail = findViewById(R.id.imgDetail);
        tvDetail = findViewById(R.id.tvDetail);
        tvContent = findViewById(R.id.tvContent);
    }

    private void initData() {
        Intent intent = getIntent();
        type = intent.getStringExtra("type");

        switch (type) {
            case Contact.INFO1:
                imgDetail.setImageResource(R.drawable.ic_know_blood);
                tvDetail.setText(R.string.know_blood_sugar);
                tvContent.setText(R.string.content_know_blood_sugar);
                break;

            case Contact.INFO2:
                imgDetail.setImageResource(R.drawable.ic_low_blood);
                tvDetail.setText(R.string.low_blood_sugar);
                tvContent.setText(R.string.content_low_blood_sugar);
                break;

            case Contact.INFO3:
                imgDetail.setImageResource(R.drawable.ic_high_blood);
                tvDetail.setText(R.string.high_blood_sugar);
                tvContent.setText(R.string.content_high_blood_sugar);
                break;

            case Contact.INFO4:
                imgDetail.setImageResource(R.drawable.ic_monitoring);
                tvDetail.setText(R.string.monitoring_blood_sugar);
                tvContent.setText(R.string.content_monitoring_blood_sugar);
                break;

            case Contact.INFO5:
                imgDetail.setImageResource(R.drawable.ic_knowledge);
                tvDetail.setText(R.string.knowledge_of_diabetes);
                tvContent.setText(R.string.content_knowledge_of_diabetes);
                break;

            case Contact.INFO6:
                imgDetail.setImageResource(R.drawable.ic_low_hypog);
                tvDetail.setText(R.string.low_hypoglycemia);
                tvContent.setText(R.string.content_low_hypoglycemia);
                break;

            case Contact.INFO7:
                imgDetail.setImageResource(R.drawable.ic_treat);
                tvDetail.setText(R.string.treat_low_blood_sugar);
                tvContent.setText(R.string.content_treat_low_blood_sugar);
                break;

            case Contact.INFO8:
                imgDetail.setImageResource(R.drawable.ic_prediabetes);
                tvDetail.setText(R.string.prediabetes);
                tvContent.setText(R.string.content_prediabetes);
                break;

            case Contact.INFO9:
                imgDetail.setImageResource(R.drawable.ic_type_diabetes);
                tvDetail.setText(R.string.type_diabetes);
                tvContent.setText(R.string.content_type_diabetes);
                break;

            case Contact.INFO10:
                imgDetail.setImageResource(R.drawable.ic_type_diabetess);
                tvDetail.setText(R.string.type_diabetess);
                tvContent.setText(R.string.content_type_diabetess);
                break;

            case Contact.INFO11:
                imgDetail.setImageResource(R.drawable.ic_gestational);
                tvDetail.setText(R.string.gestational);
                tvContent.setText(R.string.content_gestational);
                break;
        }

        btnBackDetail.setOnClickListener(v -> {
            finish();
        });
    }
}
