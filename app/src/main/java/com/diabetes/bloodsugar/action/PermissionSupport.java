package com.diabetes.bloodsugar.action;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class PermissionSupport {

    public static final int READ_PERMISSIONS_STORAGE = 1;
    private static Activity activity;
    private static PermissionSupport support;


    public PermissionSupport(Activity activity) {
        this.activity = activity;
    }

    public static PermissionSupport getInstall(Activity activity) {
        if (support == null || activity != support.activity)
            support = new PermissionSupport(activity);
        return support;
    }

    private static Boolean hasPermissionGranted(String permission) {
        Boolean status = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
        Log.d("PermissionSupport", "hasPermissionGranted: " + permission + ": " + status);
        return status;
    }

    public static Boolean requestPermissionStore() {
        if (hasPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) return true;
        if (Build.VERSION.SDK_INT < 23) return true;

        activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                READ_PERMISSIONS_STORAGE);
        return false;
    }
}
