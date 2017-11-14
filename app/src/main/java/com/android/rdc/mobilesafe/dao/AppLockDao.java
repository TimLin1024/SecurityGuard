package com.android.rdc.mobilesafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.android.rdc.mobilesafe.constant.Constant;
import com.android.rdc.mobilesafe.db.AppLockOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class AppLockDao {
    private Context mContext;
    // TODO: 2017/7/4 0004  使用 ContentResolver 暴露数据，在 已加锁页面，未加锁页面、程序锁界面 使用该 uri 进行注册内容观察者
    private Uri mUri = Uri.parse(Constant.URI_STR);
    private static final String COLUMN_NAME = "packagename";
    private static final String TABLE_NAME = "applock";
    private SQLiteDatabase db;

    private volatile static AppLockDao sInstance;


    private AppLockDao(Context context) {
        mContext = context;
        AppLockOpenHelper mAppLockOpenHelper = new AppLockOpenHelper(context);
        db = mAppLockOpenHelper.getWritableDatabase();
    }

    public static AppLockDao getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AppLockDao.class) {
                if (sInstance == null) {
                    sInstance = new AppLockDao(context);
                }
            }
        }
        return sInstance;
    }

    public boolean insert(String packageName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, packageName);
        long rowId = db.insert(TABLE_NAME, null, contentValues);
        if (rowId == -1) {
            return false;
        } else {
            mContext.getContentResolver().notifyChange(mUri, null);
            return true;
        }
    }

    public boolean delete(String packageName) {
        int rowNum = db.delete(TABLE_NAME, COLUMN_NAME + "=?", new String[]{packageName});
        if (rowNum == 0) {
            return false;
        } else {
            mContext.getContentResolver().notifyChange(mUri, null);
            return true;
        }
    }

    public boolean find(String packageName) {
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_NAME + "=?", new String[]{packageName}, null, null, null);
        if (cursor.moveToNext()) {
            cursor.close();
            return true;

        } else {
            cursor.close();
            return false;
        }
    }

    public List<String> findAll() {
        List<String> packageList = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                packageList.add(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return packageList;
    }
}
