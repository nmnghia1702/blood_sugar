package com.diabetes.bloodsugar.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.diabetes.bloodsugar.R;
import com.diabetes.bloodsugar.action.Contact;
import com.diabetes.bloodsugar.activity.NewEditRecordActivity;
import com.diabetes.bloodsugar.adapter.MainEditAdapter;
import com.diabetes.bloodsugar.bean.BloodSugarObj;
import com.diabetes.bloodsugar.db.DbSupport;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.telpoo.frame.object.BaseObject;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements View.OnClickListener, OnChartValueSelectedListener {

    static ArrayList<BaseObject> listBloodSugar;
    MainEditAdapter mainEditAdapter;
    TextView tvNumberDay, tvNumberAverageDay, tvNumberWeek, tvNumberMonth, tvNumberYear, tvNumberAll, tvView;
    RecyclerView rvEdit;
    ImageView btnAdd;
    HorizontalScrollView hzScrollView;
    static int sum, sumDay, sumAverageDay, sumWeek, sumMonth;
    private CombinedChart mChart;


    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initView(view);
        initData();
        return view;
    }

    private void initView(View view) {
        tvNumberDay = view.findViewById(R.id.tvNumberDay);
        tvNumberAverageDay = view.findViewById(R.id.tvNumberAverageDay);
        tvNumberWeek = view.findViewById(R.id.tvNumberWeek);
        tvNumberMonth = view.findViewById(R.id.tvNumberMonth);
        tvNumberYear = view.findViewById(R.id.tvNumberYear);
        tvNumberAll = view.findViewById(R.id.tvNumberAll);
        rvEdit = view.findViewById(R.id.rvEdit);
        btnAdd = view.findViewById(R.id.btnAdd);
        mChart = view.findViewById(R.id.combinedChart);
        tvView = view.findViewById(R.id.tvView);
    }

    private void drawLineChart() {
        mChart.getDescription().setEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);
        mChart.setHighlightFullBarEnabled(false);
        mChart.setOnChartValueSelectedListener(this);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);

        final List<String> xLabel = new ArrayList<>();
        xLabel.add("Jan");
        xLabel.add("Feb");
        xLabel.add("Mar");
        xLabel.add("Apr");
        xLabel.add("May");
        xLabel.add("Jun");
        xLabel.add("Jul");
        xLabel.add("Aug");
        xLabel.add("Sep");
        xLabel.add("Oct");
        xLabel.add("Nov");
        xLabel.add("Dec");

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xLabel.get((int) value % xLabel.size());
            }
        });

        CombinedData data = new CombinedData();
        LineData lineDatas = new LineData();
        lineDatas.addDataSet((ILineDataSet) dataChart());

        data.setData(lineDatas);

        xAxis.setAxisMaximum(data.getXMax() + 0.25f);

        mChart.setData(data);
        mChart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Toast.makeText(getActivity(), "" + e.getY(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {

    }

    private static DataSet dataChart() {
        LineDataSet set = null;
        try {
            LineData d = new LineData();
            int[] data = new int[]{numberOper(0), sumMonth, sumMonth, sumMonth, sumMonth, sumMonth, sumMonth, sumMonth, sumMonth, sumMonth, sumMonth, sumMonth};
            ArrayList<Entry> entries = new ArrayList<Entry>();

            for (int index = 0; index < 12; index++) {
                entries.add(new Entry(index, data[index]));
            }

            set = new LineDataSet(entries, "Request Ots approved");
            set.setColor(Color.BLUE);
            set.setLineWidth(2.5f);
            set.setCircleColor(Color.BLUE);
            set.setCircleRadius(5f);
            set.setFillColor(Color.BLUE);
            set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set.setDrawValues(true);
            set.setValueTextSize(10f);
            set.setValueTextColor(Color.BLUE);

            set.setAxisDependency(YAxis.AxisDependency.LEFT);
            d.addDataSet(set);
        } catch (Exception e) {

        }
        return set;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initData() {
        listBloodSugar = DbSupport.getListBloodSugar();

        rvEdit.setHasFixedSize(true);
        rvEdit.setItemAnimator(new DefaultItemAnimator());
        rvEdit.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mainEditAdapter = new MainEditAdapter(getActivity(), this, listBloodSugar);
        rvEdit.setAdapter(mainEditAdapter);
        mainEditAdapter.notifyDataSetChanged();

        btnAdd.setOnClickListener(this);

        try {
            tvNumberDay.setText(listBloodSugar.get(0).get(BloodSugarObj.number).replace(".0", "") + ".0");
            sum = numberOper(0) + numberOper(1) + numberOper(2);
            sumAverageDay = sum / 3;
            tvNumberAverageDay.setText(sumAverageDay + ".0");

            sum = numberOper(0) + numberOper(1) + numberOper(2) + numberOper(3) + numberOper(4) + numberOper(5) + numberOper(6);
            sumWeek = sum / 7;
            tvNumberWeek.setText(sumWeek + ".0");

            sum = numberOper(0) + numberOper(1) + numberOper(2) + numberOper(3) + numberOper(4) + numberOper(5) + numberOper(6) + numberOper(7) +
                    numberOper(8) + numberOper(9) + numberOper(10) + numberOper(11) + numberOper(12) + numberOper(13) + numberOper(14) + numberOper(15) +
                    numberOper(16) + numberOper(17) + numberOper(18) + numberOper(19) + numberOper(20) + numberOper(21) + numberOper(22) + numberOper(23) +
                    numberOper(24) + numberOper(25) + numberOper(26) + numberOper(27) + numberOper(28) + numberOper(29);
            sumMonth = sum / 30;
            tvNumberMonth.setText(sumMonth + ".0");
            tvNumberYear.setText(sumMonth + ".0");
            tvNumberAll.setText(sumMonth + ".0");
        } catch (Exception e) {
        }

        if (listBloodSugar.size() == 0) {
            tvView.setVisibility(View.VISIBLE);
        } else {
            tvView.setVisibility(View.GONE);
        }

        drawLineChart();
    }

    public static int numberOper(int i) {
        return Integer.parseInt(listBloodSugar.get(i).get(BloodSugarObj.number).replace(".0", ""));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                Intent intent = new Intent(getActivity(), NewEditRecordActivity.class);
                intent.putExtra("type", Contact.ADDRECORD);
                startActivityForResult(intent, 1);
                break;
        }
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
