package com.android.rdc.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TrafficOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "traffic.db";
    public static final String TABLE_NAME = "traffic";


    public TrafficOpenHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(id integer primary key autoincrement, traffic varchar(255), date datetime)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
