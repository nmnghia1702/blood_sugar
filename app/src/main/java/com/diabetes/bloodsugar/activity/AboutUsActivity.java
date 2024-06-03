package com.diabetes.bloodsugar.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.diabetes.bloodsugar.BuildConfig;
import com.diabetes.bloodsugar.R;

public class AboutUsActivity extends AppCompatActivity {
    ImageView btnBack;
    TextView tvVersion;
    TextView tvPolicy, tvTerms;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        initView();
        initData();
    }

    private void initView() {
        btnBack = findViewById(R.id.btnBack);
        tvVersion = findViewById(R.id.tvVersion);
        tvPolicy = findViewById(R.id.tvPolicy);
        tvTerms = findViewById(R.id.tvTerms);
    }

    private void initData() {
        tvVersion.setText("V " + BuildConfig.VERSION_NAME);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String urlPolicy = "";
                if (urlPolicy.isEmpty()) return;
                uri = Uri.parse(urlPolicy);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });
        tvPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String urlTems = "";
                if (urlTems.isEmpty()) return;
                uri = Uri.parse(urlTems);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });
    }
}