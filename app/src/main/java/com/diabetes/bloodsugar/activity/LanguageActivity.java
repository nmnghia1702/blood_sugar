package com.diabetes.bloodsugar.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.mnpemods.mcpecenter.model.LanguageModel;
import com.diabetes.bloodsugar.R;
import com.diabetes.bloodsugar.adapter.LanguageAdapter;
import com.diabetes.bloodsugar.model.IClickLanguage;
import com.diabetes.bloodsugar.utils.SystemUtil;

import java.util.ArrayList;
import java.util.List;

public class LanguageActivity extends AppCompatActivity implements IClickLanguage {
    Toolbar toolBar;
    MenuItem item;
    LanguageAdapter adapter;
    LanguageModel model = new LanguageModel();
    RecyclerView rcl_language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        initview();
        initData();
    }

    private void initview() {
        toolBar = findViewById(R.id.toolBar);
//        toolBar.setPadding(0, SPRSave.getStatusBarHeight(LanguageActivity.this), 0, 0);
        toolBar.setTitle("");
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        rcl_language = findViewById(R.id.rcl_language);

    }

    private void initData() {
        rcl_language.setHasFixedSize(true);
        rcl_language.setItemAnimator(new DefaultItemAnimator());
        rcl_language.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LanguageAdapter(this, setLanguageDefault(), this);
        rcl_language.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_continer:
                final Intent intent = new Intent(LanguageActivity.this, IntroduceActivity.class);
                startActivity(intent);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_country, menu);
        item = menu.findItem(R.id.action_continer);
        item.setVisible(true);
        return true;
    }

    private List<LanguageModel> setLanguageDefault() {
        List<LanguageModel> lists = new ArrayList<>();
        String key = SystemUtil.getPreLanguage(this);
        lists.add(new LanguageModel(getString(R.string.england), "en", false, R.drawable.ic_language_english));
        lists.add(new LanguageModel(getString(R.string.portugal), "pt", false, R.drawable.ic_language_portuguese));
        lists.add(new LanguageModel(getString(R.string.germany), "de", false, R.drawable.ic_language_german));
        lists.add(new LanguageModel(getString(R.string.korea), "ko", false, R.drawable.ic_language_korean));
        lists.add(new LanguageModel(getString(R.string.japan), "ja", false, R.drawable.ic_language_japanese));
        lists.add(new LanguageModel(getString(R.string.spain), "es", false, R.drawable.ic_language_spanish));
        lists.add(new LanguageModel(getString(R.string.india), "hi", false, R.drawable.ic_language_hindi));
        lists.add(new LanguageModel(getString(R.string.other), "other", false, R.drawable.ic_language_other));
        for (int i = 0; i < lists.size(); i++) {
            if (key.compareTo(lists.get(i).getIsoLanguage()) == 0) {
                LanguageModel data = lists.get(i);
                data.setCheck(true);
                lists.remove(lists.get(i));
                lists.add(0, data);
            }
        }
        return lists;
    }

    @Override
    public void onClick(LanguageModel data) {
        adapter.setSelectLanguage(data);
        model = data;
    }
}