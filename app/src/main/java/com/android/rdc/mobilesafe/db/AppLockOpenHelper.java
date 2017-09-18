package com.android.rdc.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppLockOpenHelper extends SQLiteOpenHelper {

    private static final String CREATE_TABLE = "CREATE TABLE applock(id INTEGER PRIMARY KEY AUTOINCREMENT ,packagename varchar(20) )";

//    private volatile static AppLockOpenHelper sAppLockOpenHelper;
//
//    public static AppLockOpenHelper getInstance(Context context) {
//        if (sAppLockOpenHelper == null) {
//            synchronized (AppLockOpenHelper.class) {
//                if (sAppLockOpenHelper == null) {
//                    sAppLockOpenHelper = new AppLockOpenHelper(context);
//                }
//            }
//        }
//        return sAppLockOpenHelper;
//    }

    public AppLockOpenHelper(Context context) {
        super(context, "applock.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
