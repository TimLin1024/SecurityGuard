package com.android.rdc.mobilesafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.rdc.mobilesafe.db.BlackNumberOpenHelper;
import com.android.rdc.mobilesafe.bean.BlackContactInfo;

import java.util.ArrayList;
import java.util.List;

public class BlackNumberDao {
    private SQLiteOpenHelper mSQLiteOpenHelper;

    private static final String KEY_NUMBER = "number";
    private static final String KEY_NAME = "name";
    private static final String KEY_MODE = "mode";
    private static final String TABLE_NAME = "blackNumber";

    public BlackNumberDao(Context context) {
        mSQLiteOpenHelper = new BlackNumberOpenHelper(context);
    }

    public boolean add(BlackContactInfo contactInfo) {
        ContentValues values = new ContentValues();
        String phoneNumber = contactInfo.getPhoneNumber();
        if (phoneNumber.startsWith("+86")) {//去掉 +86 的前缀
            contactInfo.setPhoneNumber(phoneNumber.substring(3, phoneNumber.length()));
        }
        values.put(KEY_NUMBER, contactInfo.getPhoneNumber());
        values.put(KEY_NAME, contactInfo.getContractName());
        values.put(KEY_MODE, contactInfo.getMode());
        return mSQLiteOpenHelper.getWritableDatabase().insert(TABLE_NAME, null, values) != -1;
    }

    public boolean delete(BlackContactInfo contactInfo) {
        return mSQLiteOpenHelper.getWritableDatabase().
                delete(TABLE_NAME, KEY_NUMBER + "=?", new String[]{contactInfo.getPhoneNumber()}) != 0;
    }

    public List<BlackContactInfo> getPagesBlackNumber(int pageNumber, int pageSize) {
        SQLiteDatabase sqLiteDatabase = mSQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT number,mode,name FROM " + TABLE_NAME + " LIMIT ? OFFSET ?",
                new String[]{String.valueOf(pageSize), String.valueOf(pageNumber * pageSize)});
        List<BlackContactInfo> blackContactInfoList = new ArrayList<>();
        while (cursor.moveToNext()) {
            BlackContactInfo contactInfo = new BlackContactInfo();
            contactInfo.setPhoneNumber(cursor.getString(0));
            contactInfo.setMode(cursor.getInt(1));
            contactInfo.setContractName(cursor.getString(2));
            blackContactInfoList.add(contactInfo);
        }
        return blackContactInfoList;
    }

    public boolean isNumberExist(String number) {
        SQLiteDatabase db = mSQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, "number=?",
                new String[]{number}, null, null, null);
        if (cursor.moveToNext()) {
            cursor.close();
            db.close();
            return true;
        }
        cursor.close();
        db.close();
        return false;
    }

    public int getBlackContactMode(String number) {
        SQLiteDatabase db = mSQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{"mode"}, "number=?",
                new String[]{number}, null, null, null);
        int mode = 0;
        if (cursor.moveToNext()) {
            mode = cursor.getInt(cursor.getColumnIndex(KEY_MODE));
        }
        cursor.close();
        db.close();
        return mode;
    }

    public int getTotalNumber() {
        SQLiteDatabase sqLiteDatabase = mSQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM blackNumber", null);
        cursor.moveToNext();
        int count = cursor.getInt(0);
        cursor.close();
        sqLiteDatabase.close();
        return count;
    }
}
