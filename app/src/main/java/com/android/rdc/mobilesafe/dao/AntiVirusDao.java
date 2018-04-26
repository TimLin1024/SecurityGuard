package com.android.rdc.mobilesafe.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.rdc.mobilesafe.app.App;

public class AntiVirusDao {
    private static final String TAG = "AntiVirusDao";

    /**
     * 输入程序的 md5 码
     * 如果是病毒，则输出病毒的描述
     */
    public static String checkVirus(String md5) {
        String desc = null;
        String path = App.getAppContext().getFilesDir().getPath().concat("/antivirus.db");// "/data/data/com.android.rdc.mobilesafe/files/antivirus.db"
        Log.d(TAG, "checkVirus: " + path);// "/data/data/com.android.rdc.应用名/files/antivirus.db"
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openDatabase(path, null,
                SQLiteDatabase.OPEN_READONLY);
        //desc 是 description 的意思，是表中的某一列。desc 在 AS 中会被识别为「降序关键字」，导致报错。但是，给定的数据表无法修改
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT desc FROM datable WHERE md5=?", new String[]{md5});
        if (cursor.moveToNext()) {
            desc = cursor.getString(0);
        }
        cursor.close();
        return desc;
    }
}
