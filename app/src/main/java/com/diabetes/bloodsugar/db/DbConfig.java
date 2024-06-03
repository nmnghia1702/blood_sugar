package com.diabetes.bloodsugar.db;


import com.diabetes.bloodsugar.bean.BloodSugarObj;

public class DbConfig {
    public static final String[] tables = {"bloodsugardsss"};
    public static final String[][] keys = {BloodSugarObj.keydb};
    public static final String dbName = "bloodsugarss";
    public static final Integer dbVersion = 1;
    public static final String BLOODSUGAR = tables[0];
}
