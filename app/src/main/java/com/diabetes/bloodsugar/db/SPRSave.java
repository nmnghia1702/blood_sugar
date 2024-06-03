package com.diabetes.bloodsugar.db;

import android.content.Context;

import com.telpoo.frame.utils.SPRSupport;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class SPRSave {

    public static SPRSave sprSave;
    Context context;
    public static final String is_open_ads_show = "is_open_ads_show";
    public static final String time_current = "time_current";
    public static final String time_check = "time_check";
    public static final String isGosss = "isGosss";
    public static final String isDarkMode = "isDarkMode";
    public static final String count_start = "count_start";
    public static final String is_sub = "is_sub";
    public static final String dataIp = "dataIp";
    public static final String widthScreen = "widthScreen";
    public static final String heightScreen = "heightScreen";
    public static final String statusBarHeight = "statusBarHeight";
    public static String s_categories = "s_categories";
    public static final String clear_mode = "clear_mode";
    public static final String list_mode = "list_mode";
    public static final String is_guide = "is_guide";
    public static final String ram = "ram";
    public static final String intall_app = "intall_app";
    public static final String date = "date";


    public static SPRSave getInstall(Context context) {
        if (sprSave == null) sprSave = new SPRSave(context);
        return sprSave;
    }

    public static void saveCountStart(long count, Context context) {
        SPRSupport.save(count_start, count, context);
    }

    public static long getCountStart(Context context) {
        return SPRSupport.getLong(count_start, context, 0l);
    }

    public SPRSave(Context context) {
        this.context = context;
    }

    public static void saveIsGo(boolean boo, Context context) {
        SPRSupport.save(isGosss, boo, context);
    }

    public static boolean getIsGo(Context context) {
        return SPRSupport.getBool(isGosss, context, false);
    }

    public static void saveIsOpenAdsShow(boolean boo, Context context) {
        SPRSupport.save(is_open_ads_show, boo, context);
    }

    public static boolean getIsOpenAdsShow(Context context) {
        return SPRSupport.getBool(is_open_ads_show, context, false);
    }

    public static void saveTimeCurrent(Context context) {
        long time = Calendar.getInstance().getTimeInMillis();
        SPRSupport.save(time_current, time, context);
    }

    public static long getTimeCurrent(Context context) {
        return SPRSupport.getLong(time_current, context);
    }

    public static void saveIsSub(boolean isGuide, Context context) {
        SPRSupport.save(is_sub, isGuide, context);
    }

    public static boolean getIsSub(Context context) {
        return SPRSupport.getBool(is_sub, context, false);
    }

    public static void saveDataIp(JSONObject data, Context context) {
        SPRSupport.save(dataIp, data.toString(), context);
    }

    public static JSONObject getDataIp(Context context) {
        JSONObject ads = new JSONObject();
        try {
            ads = new JSONObject(SPRSupport.getString(dataIp, context));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ads;
    }

    public static int getTimeShow(Context context) {
        return Integer.parseInt(SPRSupport.getString(time_check, context, "3"));
    }

    public static void saveIsDarkMode(boolean boo, Context context) {
        SPRSupport.save(isDarkMode, boo, context);
    }

    public static boolean getIsDarkMode(Context context) {
        return SPRSupport.getBool(isDarkMode, context, false);
    }

    public static void saveWidthScreen(int i, Context context) {
        SPRSupport.save(widthScreen, i, context);
    }

    public static int getWidthScreen(Context context) {
        return SPRSupport.getInt(widthScreen, context);
    }


    public static void saveHeightScreen(int i, Context context) {
        SPRSupport.save(heightScreen, i, context);
    }

    public static int getHeightScreen(Context context) {
        return SPRSupport.getInt(heightScreen, context);
    }

    public static void saveStatusBarHeight(int sh, Context context) {
        SPRSupport.save(statusBarHeight, sh, context);
    }

    public static int getStatusBarHeight(Context context) {
        return SPRSupport.getInt(statusBarHeight, context);
    }

    public static void saveRam(long l, Context context) {
        SPRSupport.save(ram, l, context);
    }

    public static long getRam(Context context) {
        return SPRSupport.getLong(ram, context);
    }

    public static void saveIsGuide(boolean isGuide, Context context) {
        SPRSupport.save(is_guide, isGuide, context);
    }

    public static boolean getIsGuide(Context context) {
        return SPRSupport.getBool(is_guide, context);
    }

    public static void saveListMode(boolean boo, Context context) {
        SPRSupport.save(list_mode, boo, context);
    }

    public static boolean getListMode(Context context) {
        return SPRSupport.getBool(list_mode, context);
    }

    public static void saveData(String s, Context context) {
        SPRSupport.save(s_categories, s, context);
    }

    public static String getData(Context context) {
        return SPRSupport.getString(s_categories, context);
    }

    public static void saveClearMode(boolean isClear, Context context) {
        SPRSupport.save(clear_mode, isClear, context);
    }

    public static boolean getClearMode(Context context) {
        return SPRSupport.getBool(clear_mode, context);
    }

    public static void saveDate(boolean boo, Context context) {
        SPRSupport.save(date, boo, context);
    }

    public static boolean getDate(Context context) {
        return SPRSupport.getBool(date, context, false);
    }
}
