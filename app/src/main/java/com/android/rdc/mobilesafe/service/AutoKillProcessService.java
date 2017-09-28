package com.android.rdc.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.rdc.mobilesafe.entity.TaskInfo;
import com.android.rdc.mobilesafe.util.TaskInfoParser;

import java.util.List;

public class AutoKillProcessService extends Service {
    private static final String TAG = "AutoKillProcessService";
    private ScreenLockReceiver mScreenLockReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        mScreenLockReceiver = new ScreenLockReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenLockReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mScreenLockReceiver != null) {
            unregisterReceiver(mScreenLockReceiver);
        }
        mScreenLockReceiver = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class ScreenLockReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: ");
            ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            List<TaskInfo> taskInfoList = TaskInfoParser.parseTaskInfo(AutoKillProcessService.this);
            for (TaskInfo taskInfo : taskInfoList) {//清除所有用户进程
                if (taskInfo.isUserApp() && !taskInfo.getPackageName().contains("SecurityMax")) {
                    am.killBackgroundProcesses((taskInfo.getPackageName()));
                }
            }
        }
    }
}
