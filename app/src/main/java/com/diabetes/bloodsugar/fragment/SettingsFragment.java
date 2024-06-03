package com.diabetes.bloodsugar.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.diabetes.bloodsugar.BuildConfig;
import com.diabetes.bloodsugar.R;
import com.diabetes.bloodsugar.activity.AboutUsActivity;
import com.diabetes.bloodsugar.helper.HandleHelper;

public class SettingsFragment extends Fragment {
    View btnExport, btnRate, btnShare, btnFeed, btnAbout;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        initView(view);
        initData();
        return view;
    }


    private void initView(View view) {
        btnExport = view.findViewById(R.id.btnExport);
        btnRate = view.findViewById(R.id.btnRate);
        btnShare = view.findViewById(R.id.btnShare);
        btnFeed = view.findViewById(R.id.btnFeed);
        btnAbout = view.findViewById(R.id.btnAbout);
    }

    private void initData() {
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HandleHelper.toAppInStore(getActivity(), BuildConfig.APPLICATION_ID);
            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                    startActivity(Intent.createChooser(shareIntent, "Choose one"));
                } catch (Exception e) {
                    Log.d("quanerr", e.toString());
                }
            }
        });
        btnFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AboutUsActivity.class));
            }
        });

    }

}
