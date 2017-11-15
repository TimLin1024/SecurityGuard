package com.android.rdc.mobilesafe.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;

import com.android.rdc.mobilesafe.constant.Constant;
import com.android.rdc.mobilesafe.service.AppLockService;
import com.android.rdc.mobilesafe.util.NotificationUtil;
import com.android.rdc.mobilesafe.util.SystemUtil;
import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;

public class App extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context sContext;
    private SharedPreferences mSp;

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);//初始化 Stetho
        CrashReport.initCrashReport(getApplicationContext());//初始化 bugly
        setupLeakCanary();//初始化 LeakCanary,检测内存泄漏
        sContext = getApplicationContext();
        mSp = getSharedPreferences(Constant.SP_NAME_CONFIG, MODE_PRIVATE);
        initConfig();
    }


    private void initConfig() {
        //开启通知栏显示
        if (mSp.getBoolean(Constant.KEY_SHOW_NOTIFICATION, false)) {
            NotificationCompat.Builder mBuilder = NotificationUtil.buildNotification(this);
            NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);//使用 appContext 防止内存泄漏
            manager.notify("通知", Constant.NOTIFICATION_ID, mBuilder.build());
        }
        //开启程序锁服务
        if (mSp.getBoolean(Constant.KEY_APP_LOCK_SERVICE_ON, false)) {
            if (!SystemUtil.isServiceRunning(this, Constant.APP_LOCK_SERVICE_NAME)) {
                Intent intent = new Intent(this, AppLockService.class);
                startService(intent);
            }
        }

        if (mSp.getBoolean(Constant.KEY_BLACKLIST, false)) {
            // TODO: 2017/11/15 0015  开启短信、电话拦截

        }
    }

    private void setupLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }

    public static Context getAppContext() {
        return sContext;
    }
}
