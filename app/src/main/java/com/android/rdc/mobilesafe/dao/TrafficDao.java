package com.android.rdc.mobilesafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.android.rdc.mobilesafe.db.TrafficOpenHelper;
import com.android.rdc.mobilesafe.util.DateUtil;

import java.util.Date;

public class TrafficDao {
    private TrafficOpenHelper mTrafficOpenHelper;
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TRAFFIC = "traffic";

    public TrafficDao(Context context) {
        mTrafficOpenHelper = new TrafficOpenHelper(context);
    }

    public void insertTodayTraffic(long traffic) {
        SQLiteDatabase db = mTrafficOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DATE, DateUtil.formatDate(new Date()));
        contentValues.put(COLUMN_TRAFFIC, traffic);
        db.insert(TrafficOpenHelper.TABLE_NAME, null, contentValues);
    }

    public long getTrafficByDate(String dateStr) {
        SQLiteDatabase sqLiteDatabase = mTrafficOpenHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + COLUMN_TRAFFIC + " FROM " + TrafficOpenHelper.TABLE_NAME + " WHERE date=?", new String[]{dateStr});
        long traffic = 0;
        if (cursor.moveToNext()) {
            String trafficStr = cursor.getString(0);
            if (TextUtils.isEmpty(trafficStr)) {
                traffic = -1;
            } else {
                traffic = Long.parseLong(trafficStr);
            }
        }
        cursor.close();
        sqLiteDatabase.close();
        return traffic;
    }

    public void updateTodayTraffic(long traffic) {
        SQLiteDatabase sqLiteDatabase = mTrafficOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Date date = new Date();
        contentValues.put(COLUMN_DATE, DateUtil.formatDate(date));
        contentValues.put(COLUMN_TRAFFIC, traffic);
        sqLiteDatabase.update(TrafficOpenHelper.TABLE_NAME, contentValues, "date=?", new String[]{DateUtil.formatDate(new Date())});
    }
}
