package com.diabetes.bloodsugar.db;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.diabetes.bloodsugar.bean.BloodSugarObj;
import com.telpoo.frame.database.DbCacheUrl;
import com.telpoo.frame.object.BaseObject;

import java.util.ArrayList;


public class DbSupport {

    public static final void init(Context context) {
        MyDb.init(DbConfig.tables, DbConfig.keys, context, Environment.getExternalStorageDirectory() + "/" + DbConfig.dbName, DbConfig.dbVersion);
        DbCacheUrl.initDb(context);
    }

    public static ArrayList<BaseObject> getListBloodSugar() {
        String querry = "select * from " + DbConfig.BLOODSUGAR + " ORDER BY" + " rowid" + " DESC";
        ArrayList<BaseObject> listData = MyDb.rawQuery(querry);
        return listData;
    }

    public static Boolean saveBloodSugar(BaseObject object) {
        ArrayList<BaseObject> listBloodSugar = new ArrayList<>();
        listBloodSugar.add(object);
        boolean saveBloodSugar = MyDb.addToTable(listBloodSugar, DbConfig.BLOODSUGAR);
        Log.d("DbSupport", "saveBloodSugar : " + saveBloodSugar);
        return saveBloodSugar;
    }

    public static Boolean deleteBloodSugar(String bloodSugar) {
        boolean delete = MyDb.deleteRowInTable(DbConfig.BLOODSUGAR, BloodSugarObj.desc, bloodSugar);
        Log.d("DbSupport", "delete : " + delete);
        return delete;
    }

    public static Boolean updateRow(BaseObject object) {
        boolean update = MyDb.update(object, DbConfig.BLOODSUGAR, BloodSugarObj.desc);
        Log.d("DbSupport", "update : " + update);
        return update;
    }

    public static ArrayList<BaseObject> searchOff(ArrayList<BaseObject> list, String keySearch) {
        ArrayList<BaseObject> listSugess = new ArrayList<>();
        for (BaseObject listObj : list) {
            if (listObj.get(BloodSugarObj.name).toLowerCase().contains(keySearch))
                listSugess.add(listObj);
        }
        return listSugess;
    }
}
