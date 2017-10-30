package com.android.rdc.mobilesafe.app;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.tencent.bugly.crashreport.CrashReport;

public class App extends Application {

    private static Context sContext;
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);//初始化 Stetho
        CrashReport.initCrashReport(getApplicationContext());//初始化 bugly

        sContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return sContext;
    }
}
