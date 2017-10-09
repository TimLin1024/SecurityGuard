package com.android.rdc.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.android.rdc.mobilesafe.dao.TrafficDao;

public class TrafficMonitoringService extends Service {
    private long mOldRxBytes;
    private long mOldTxBytes;
    private TrafficDao mDao;
    private SharedPreferences mSp;
    private long mUsedFlow;
    boolean mFlag = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
