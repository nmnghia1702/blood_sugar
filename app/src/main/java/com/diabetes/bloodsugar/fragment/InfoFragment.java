package com.diabetes.bloodsugar.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.diabetes.bloodsugar.R;
import com.diabetes.bloodsugar.action.Contact;
import com.diabetes.bloodsugar.activity.DetailInfoActivity;


public class InfoFragment extends Fragment implements View.OnClickListener {

    RelativeLayout rlView1, rlView2, rlView3, rlView4, rlView5, rlView6, rlView7, rlView8, rlView9, rlView10, rlView11;


    public InfoFragment() {
        // Required empty public constructor
    }

    public static InfoFragment newInstance() {
        InfoFragment fragment = new InfoFragment();
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
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        initView(view);
        initData();
        return view;
    }

    private void initView(View view) {
        rlView1 = view.findViewById(R.id.rlView1);
        rlView2 = view.findViewById(R.id.rlView2);
        rlView3 = view.findViewById(R.id.rlView3);
        rlView4 = view.findViewById(R.id.rlView4);
        rlView5 = view.findViewById(R.id.rlView5);
        rlView6 = view.findViewById(R.id.rlView6);
        rlView7 = view.findViewById(R.id.rlView7);
        rlView8 = view.findViewById(R.id.rlView8);
        rlView9 = view.findViewById(R.id.rlView9);
        rlView10 = view.findViewById(R.id.rlView10);
        rlView11 = view.findViewById(R.id.rlView11);
    }

    private void initData() {
        rlView1.setOnClickListener(this);
        rlView2.setOnClickListener(this);
        rlView3.setOnClickListener(this);
        rlView4.setOnClickListener(this);
        rlView5.setOnClickListener(this);
        rlView6.setOnClickListener(this);
        rlView7.setOnClickListener(this);
        rlView8.setOnClickListener(this);
        rlView9.setOnClickListener(this);
        rlView10.setOnClickListener(this);
        rlView11.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), DetailInfoActivity.class);
        switch (v.getId()) {
            case R.id.rlView1:
                intent.putExtra("type", Contact.INFO1);
                break;

            case R.id.rlView2:
                intent.putExtra("type", Contact.INFO2);
                break;

            case R.id.rlView3:
                intent.putExtra("type", Contact.INFO3);
                break;

            case R.id.rlView4:
                intent.putExtra("type", Contact.INFO4);
                break;

            case R.id.rlView5:
                intent.putExtra("type", Contact.INFO5);
                break;

            case R.id.rlView6:
                intent.putExtra("type", Contact.INFO6);
                break;

            case R.id.rlView7:
                intent.putExtra("type", Contact.INFO7);
                break;

            case R.id.rlView8:
                intent.putExtra("type", Contact.INFO8);
                break;

            case R.id.rlView9:
                intent.putExtra("type", Contact.INFO9);
                break;

            case R.id.rlView10:
                intent.putExtra("type", Contact.INFO10);
                break;

            case R.id.rlView11:
                intent.putExtra("type", Contact.INFO11);
                break;
        }
        startActivity(intent);
    }
}
