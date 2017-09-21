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
        String path = App.getContext().getFilesDir().getPath().concat("/antivirus.db");// "/data/data/com.android.rdc.mobilesafe/files/antivirus.db"
        Log.d(TAG, "checkVirus: " + path);
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openDatabase(path, null,
                SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT desc FROM datable WHERE md5=?", new String[]{md5});
        if (cursor.moveToNext()) {
            desc = cursor.getString(0);
        }
        cursor.close();
        return desc;
    }
}
