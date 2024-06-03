package com.diabetes.bloodsugar.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.diabetes.bloodsugar.R;
import com.diabetes.bloodsugar.action.Contact;
import com.diabetes.bloodsugar.action.Keyboard;
import com.diabetes.bloodsugar.action.Listener;
import com.diabetes.bloodsugar.action.SPRDialog;
import com.diabetes.bloodsugar.action.SettingSupport;
import com.diabetes.bloodsugar.bean.BloodSugarObj;
import com.diabetes.bloodsugar.db.DbSupport;
import com.telpoo.frame.object.BaseObject;

import java.text.DateFormat;
import java.util.Calendar;


public class NewEditRecordActivity extends AppCompatActivity implements View.OnClickListener {

    BaseObject object = new BaseObject();
    TextView tvTitle, tvTime, tvNameAdd, btnSaveAdd, tvDate, tvViewMg, viewSoName;
    RelativeLayout rlView;
    LinearLayoutCompat lnType, lnLoadMg;
    EditText edtNumber;
    ImageView btnBack, btnTagetRange, viewBlue, viewGreen, viewOrange, viewRed, btnDelete;
    String type, typeAdd, typeEd, desc, number, name, date, time, mgDL, soName;
    boolean isClick = false;
    int myNum = 0;
    int myNumEd = 0;

    DateFormat fmtDate = DateFormat.getDateInstance();
    DateFormat fmtTime = DateFormat.getTimeInstance(DateFormat.SHORT);

    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener dates = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    TimePickerDialog.OnTimeSetListener times = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            myCalendar.set(Calendar.MINUTE, minute);
            updateLabel();
        }
    };

    private void updateLabel() {
        tvDate.setText(fmtDate.format(myCalendar.getTime()));
        tvTime.setText(fmtTime.format(myCalendar.getTime()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_record);

        initView();
        initData();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        rlView = findViewById(R.id.rlView);
        lnType = findViewById(R.id.lnType);
        tvTitle = findViewById(R.id.tvTitle);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        tvViewMg = findViewById(R.id.tvViewMg);
        tvNameAdd = findViewById(R.id.tvNameAdd);
        edtNumber = findViewById(R.id.edtNumber);
        btnSaveAdd = findViewById(R.id.btnSaveAdd);
        btnBack = findViewById(R.id.btnBack);
        lnLoadMg = findViewById(R.id.lnLoadMg);
        btnTagetRange = findViewById(R.id.btnTagetRange);
        viewBlue = findViewById(R.id.viewBlue);
        viewGreen = findViewById(R.id.viewGreen);
        viewOrange = findViewById(R.id.viewOrange);
        viewRed = findViewById(R.id.viewRed);
        viewSoName = findViewById(R.id.viewSoName);
        btnDelete = findViewById(R.id.btnDelete);
        edtNumber.setText("72.0");

        type = getIntent().getStringExtra("type");
        object = getIntent().getParcelableExtra("data");

        rlView.setOnTouchListener((view1, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_POINTER_DOWN:
                case MotionEvent.ACTION_MOVE:
                    Keyboard.hideKeyboard(this);
                    break;
            }
            return false;
        });

        updateLabel();
    }

    private void initData() {
        switch (type) {
            case Contact.ADDRECORD:
                tvTitle.setText(R.string.new_record);
                btnDelete.setVisibility(View.GONE);
                typeAdd = Contact.MGDL;
                typeEd = "";
                break;

            case Contact.EDITRECORD:
                tvTitle.setText(R.string.edit_record);
                btnDelete.setVisibility(View.VISIBLE);
                tvNameAdd.setText(object.get(BloodSugarObj.name));
                number = object.get(BloodSugarObj.number);
                edtNumber.setText(number);
                tvTime.setText(object.get(BloodSugarObj.time));
                tvDate.setText(object.get(BloodSugarObj.date));
                try {
                    myNumEd = Integer.parseInt(number);
                    if (myNumEd < 72) {
                        viewSoName.setText("mg/dL<72.0");
                        viewBlue.setVisibility(View.VISIBLE);
                        viewGreen.setVisibility(View.GONE);
                        viewOrange.setVisibility(View.GONE);
                        viewRed.setVisibility(View.GONE);
                    }

                    if (myNumEd >= 72) {
                        viewSoName.setText("72.0<=mg/dL<99.0");
                        viewGreen.setVisibility(View.VISIBLE);
                        viewBlue.setVisibility(View.GONE);
                        viewOrange.setVisibility(View.GONE);
                        viewRed.setVisibility(View.GONE);
                    }

                    if (myNumEd > 98) {
                        viewSoName.setText("99.0<=mg/dL<126.0");
                        viewOrange.setVisibility(View.VISIBLE);
                        viewGreen.setVisibility(View.GONE);
                        viewBlue.setVisibility(View.GONE);
                        viewRed.setVisibility(View.GONE);
                    }

                    if (myNumEd > 125) {
                        viewSoName.setText("mg/dL>=126.0");
                        viewRed.setVisibility(View.VISIBLE);
                        viewOrange.setVisibility(View.GONE);
                        viewGreen.setVisibility(View.GONE);
                        viewBlue.setVisibility(View.GONE);
                    }
                } catch (NumberFormatException nfed) {
                    Log.d("ThaoTm", "nfed: " + nfed.getMessage());
                }

//                typeEd = object.get(BloodSugarObj.mgdl);
                typeEd = Contact.MGDL;
                typeAdd = "";
                desc = object.get(BloodSugarObj.desc);
                break;
        }

        lnType.setOnClickListener(this);
        tvDate.setOnClickListener(this);
        tvTime.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnSaveAdd.setOnClickListener(this);
//        lnLoadMg.setOnClickListener(this);
        btnTagetRange.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        edtNumber.addTextChangedListener(onTextChangedNumber());
    }

    private TextWatcher onTextChangedNumber() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    myNum = Integer.parseInt(s.toString());
                    if (myNum < 72) {
                        Log.d("ThaoTm", "71: " + myNum);
                        tvNameAdd.setText(R.string.low);
                        viewSoName.setText("mg/dL<72.0");
                        viewBlue.setVisibility(View.VISIBLE);
                        viewGreen.setVisibility(View.GONE);
                        viewOrange.setVisibility(View.GONE);
                        viewRed.setVisibility(View.GONE);
                    }

                    if (myNum >= 72) {
                        Log.d("ThaoTm", "98: " + myNum);
                        tvNameAdd.setText(R.string.normal);
                        viewSoName.setText("72.0<=mg/dL<99.0");
                        viewGreen.setVisibility(View.VISIBLE);
                        viewBlue.setVisibility(View.GONE);
                        viewOrange.setVisibility(View.GONE);
                        viewRed.setVisibility(View.GONE);
                    }

                    if (myNum > 98) {
                        Log.d("ThaoTm", "125: " + myNum);
                        tvNameAdd.setText(R.string.pre_diabetes);
                        viewSoName.setText("99.0<=mg/dL<126.0");
                        viewOrange.setVisibility(View.VISIBLE);
                        viewGreen.setVisibility(View.GONE);
                        viewBlue.setVisibility(View.GONE);
                        viewRed.setVisibility(View.GONE);
                    }

                    if (myNum > 125) {
                        Log.d("ThaoTm", "125: " + myNum);
                        tvNameAdd.setText(R.string.diabetes);
                        viewSoName.setText("mg/dL>=126.0");
                        viewRed.setVisibility(View.VISIBLE);
                        viewOrange.setVisibility(View.GONE);
                        viewGreen.setVisibility(View.GONE);
                        viewBlue.setVisibility(View.GONE);
                    }
                } catch (NumberFormatException nfe) {
                    Log.d("ThaoTm", "nfe: " + nfe.getMessage());
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSaveAdd:
                switch (type) {
                    case Contact.ADDRECORD:
                        if (!isValidate()) return;
                        BaseObject object = new BaseObject();
                        object.set(BloodSugarObj.number, number);
                        object.set(BloodSugarObj.time, time);
                        object.set(BloodSugarObj.date, date);
                        object.set(BloodSugarObj.name, name);
                        object.set(BloodSugarObj.mgdl, mgDL);
                        object.set(BloodSugarObj.desc, Calendar.getInstance().getTimeInMillis());

                        Log.d("ThaoTm", "onClick: " + object.toJson());
                        DbSupport.saveBloodSugar(object);
                        SettingSupport.showToast(this, "Success!");
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                        break;

                    case Contact.EDITRECORD:
                        if (!isValidate()) return;
                        BaseObject objUpdate = new BaseObject();
                        objUpdate.set(BloodSugarObj.number, number);
                        objUpdate.set(BloodSugarObj.time, time);
                        objUpdate.set(BloodSugarObj.date, date);
                        objUpdate.set(BloodSugarObj.name, name);
                        objUpdate.set(BloodSugarObj.mgdl, mgDL);
                        objUpdate.set(BloodSugarObj.desc, desc);
                        DbSupport.updateRow(objUpdate);
                        SettingSupport.showToast(this, "Update Success!");
                        Intent returnIt = new Intent();
                        setResult(Activity.RESULT_OK, returnIt);
                        finish();
                        break;
                }
                break;

            case R.id.lnType:
                SPRDialog.showDialogViewAddType(this, new Listener.OnDialogYesNoListener() {
                    @Override
                    public void onYesClick() {

                    }

                    @Override
                    public void onNoClick() {

                    }
                });
                break;

            case R.id.tvDate:
                new DatePickerDialog(this, R.style.datepicker, dates,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;

            case R.id.tvTime:
                new TimePickerDialog(this, R.style.datepicker, times,
                        myCalendar.get(Calendar.HOUR_OF_DAY),
                        myCalendar.get(Calendar.MINUTE), true).show();
                break;

            case R.id.lnLoadMg:
                if (isClick) {
                    isClick = false;
                    tvViewMg.setText("mg/dL");
                    edtNumber.setText("72.0");
                    typeAdd = Contact.MGDL;
                    typeEd = Contact.MGDL;
                } else {
                    isClick = true;
                    tvViewMg.setText("mmol/L");
                    edtNumber.setText("4.0");
                    typeAdd = Contact.MMOLL;
                    typeEd = Contact.MMOLL;
                }
                break;

            case R.id.btnTagetRange:
                Intent intent = new Intent(this, TagetRangeActivity.class);
                intent.putExtra("type", typeAdd);
                intent.putExtra("typee", typeEd);
                startActivity(intent);
                break;

            case R.id.btnDelete:
                SPRDialog.showDialogYesNo(this, getString(R.string.type_delete), getString(R.string.yes), getString(R.string.no), new Listener.OnDialogYesNoListener() {
                    @Override
                    public void onYesClick() {
                        DbSupport.deleteBloodSugar(object.get(BloodSugarObj.desc));
                        SettingSupport.showToast(NewEditRecordActivity.this, "Delete Success!");
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }

                    @Override
                    public void onNoClick() {

                    }
                });
                break;

            case R.id.btnBack:
                finish();
                break;
        }
    }

    public boolean isValidate() {
        number = edtNumber.getText().toString();
        name = tvNameAdd.getText().toString();
        time = tvTime.getText().toString();
        date = tvDate.getText().toString();
        mgDL = tvViewMg.getText().toString();
        soName = viewSoName.getText().toString();
        if (number.isEmpty()) {
            edtNumber.setError("Chưa nhập dữ liệu!");
            return false;
        }
        return true;
    }
}
