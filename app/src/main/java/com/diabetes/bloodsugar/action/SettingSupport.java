package com.diabetes.bloodsugar.action;

import android.app.Activity;
import android.widget.Toast;

public class SettingSupport {
    public static void showToast(Activity activity, String message) {
        if (message != null)
            Toast.makeText(activity, "" + message, Toast.LENGTH_LONG).show();
    }
}
