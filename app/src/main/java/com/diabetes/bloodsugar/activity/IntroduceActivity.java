package com.diabetes.bloodsugar.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.diabetes.bloodsugar.R;
import com.diabetes.bloodsugar.adapter.ViewPagerAdapter;
import com.diabetes.bloodsugar.db.SPRSave;
import com.zhpan.indicator.IndicatorView;
import com.zhpan.indicator.enums.IndicatorSlideMode;
import com.zhpan.indicator.enums.IndicatorStyle;
import com.zhpan.indicator.option.IndicatorOptions;

public class IntroduceActivity extends AppCompatActivity implements View.OnClickListener {
    ViewPager viewPagerMain;
    IndicatorView indicatorView;
    TextView tvType, tvContent;
    TextView btnNext, btbSkip;
    int[] images = {R.drawable.d0, R.drawable.d1, R.drawable.d2};
    String[] type = {"Data Tracking and Recording", "professional Chart Analysis", "Learning about Blood Sugar"};
    String[] content = {"Track, record and analyze your blood sugar", "Observe blood sugar trends more clarity", "Learn profrssional health knowledge and lifestyle"};
    ViewPagerAdapter mViewPagerAdapter;
    int curentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);
        initView();
        initData();
    }


    private void initView() {
        viewPagerMain = findViewById(R.id.viewPagerMain);
        indicatorView = findViewById(R.id.indicatorView);
        tvType = findViewById(R.id.tvType);
        tvContent = findViewById(R.id.tvContent);
        btnNext = findViewById(R.id.btnNext);
        btbSkip = findViewById(R.id.btbSkip);
        mViewPagerAdapter = new ViewPagerAdapter(IntroduceActivity.this, images, type, content);
        // Adding the Adapter to the ViewPager
        viewPagerMain.setAdapter(mViewPagerAdapter);
        indicatorView.setupWithViewPager(viewPagerMain);
        indicatorSetting(indicatorView);

    }

    private void initData() {
        viewPagerMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                curentPage = position;
                switch (position) {
                    case 0:
                    case 1:
                        btnNext.setText(R.string.next);
                        break;
                    case 2:
                        btnNext.setText(R.string.letsgo);
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        btnNext.setOnClickListener(this);
        btbSkip.setOnClickListener(this);
    }

    public void indicatorSetting(IndicatorView indi) {
        IndicatorOptions indicatorOptions = new IndicatorOptions();
        indicatorOptions.setSliderColor(getResources().getColor(R.color.light_blue_100), getResources().getColor(R.color.light_blue_600));
        indicatorOptions.setSliderWidth(40);
        indicatorOptions.setSliderHeight(20);
        indicatorOptions.setSlideMode(IndicatorSlideMode.SMOOTH);
        indicatorOptions.setIndicatorStyle(IndicatorStyle.ROUND_RECT);
        indicatorOptions.setPageSize(mViewPagerAdapter.getCount());
        indi.setIndicatorOptions(indicatorOptions);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnNext:
                if (curentPage == 2)
                    startActivity();
                viewPagerMain.setCurrentItem(curentPage + 1);
                break;
            case R.id.btbSkip:
                startActivity();
                break;
        }
    }

    void startActivity() {
        SPRSave.saveIsGo(true, IntroduceActivity.this);
        startActivity(new Intent(IntroduceActivity.this, MainActivity.class));
    }
}