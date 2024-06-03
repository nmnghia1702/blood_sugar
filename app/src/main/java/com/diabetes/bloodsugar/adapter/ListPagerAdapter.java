package com.diabetes.bloodsugar.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.diabetes.bloodsugar.fragment.HomeFragment;
import com.diabetes.bloodsugar.fragment.InfoFragment;
import com.diabetes.bloodsugar.fragment.SettingsFragment;

public class ListPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public ListPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return HomeFragment.newInstance();
            case 1:
                return InfoFragment.newInstance();
            case 2:
                return SettingsFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
