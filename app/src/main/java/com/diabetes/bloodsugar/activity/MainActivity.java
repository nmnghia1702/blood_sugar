package com.diabetes.bloodsugar.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.diabetes.bloodsugar.R;
import com.diabetes.bloodsugar.action.Contact;
import com.diabetes.bloodsugar.action.PermissionSupport;
import com.diabetes.bloodsugar.adapter.ListPagerAdapter;
import com.diabetes.bloodsugar.db.DbSupport;


public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewpager;
    private ListPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!PermissionSupport.getInstall(this).requestPermissionStore()) {
            return;
        }
        initView();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        viewpager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabLayout);
        setSupportActionBar(toolbar);

        DbSupport.init(this);
        setupTabIcons();
        loadData();
    }

    private void loadData() {
        tabLayout.getTabAt(0).setIcon(Contact.tabIcons[3]);
        tabLayout.getTabAt(1).setIcon(Contact.tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(Contact.tabIcons[2]);
        adapter = new ListPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewpager.setAdapter(adapter);
        viewpager.setOffscreenPageLimit(3);
        viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) tab.setCustomView(R.layout.view_home_tab);
        }
    }

    private void setupTabIcons() {
        tabLayout.addTab(tabLayout.newTab().setIcon(Contact.tabIcons[0]));
        tabLayout.addTab(tabLayout.newTab().setIcon(Contact.tabIcons[1]));
        tabLayout.addTab(tabLayout.newTab().setIcon(Contact.tabIcons[2]));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewpager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        setTitle("Blood Sugar");
                        tabLayout.getTabAt(0).setIcon(Contact.tabIcons[3]);
                        tabLayout.getTabAt(1).setIcon(Contact.tabIcons[1]);
                        tabLayout.getTabAt(2).setIcon(Contact.tabIcons[2]);
                        break;
                    case 1:
                        setTitle("Info");
                        tabLayout.getTabAt(0).setIcon(Contact.tabIcons[0]);
                        tabLayout.getTabAt(1).setIcon(Contact.tabIcons[4]);
                        tabLayout.getTabAt(2).setIcon(Contact.tabIcons[2]);
                        break;
                    case 2:
                        setTitle("Settings");
                        tabLayout.getTabAt(0).setIcon(Contact.tabIcons[0]);
                        tabLayout.getTabAt(1).setIcon(Contact.tabIcons[1]);
                        tabLayout.getTabAt(2).setIcon(Contact.tabIcons[5]);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnTime:
                startActivity(new Intent(this, RemindMeActivity.class));
                return true;

            case R.id.mnHistory:
                startActivity(new Intent(this, HistoryActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PermissionSupport.READ_PERMISSIONS_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initView();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    AlertDialog.Builder b = new AlertDialog.Builder(this);
                    b.setTitle("Thông Báo");
                    b.setMessage("Bạn cần cấp quyền bộ nhớ máy để tiếp tục sử dụng ứng dụng!");
                    b.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            PermissionSupport.getInstall(MainActivity.this).requestPermissionStore();
                            dialog.cancel();
                        }
                    });
                    b.setNegativeButton("Không đồng ý", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            finish();
                        }
                    });
                    AlertDialog al = b.create();
                    al.show();
                }
                break;
        }
    }
}
