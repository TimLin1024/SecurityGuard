package com.android.rdc.mobilesafe.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;

public class App extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);//初始化 Stetho
        CrashReport.initCrashReport(getApplicationContext());//初始化 bugly
        setupLeakCanary();//初始化 LeakCanary,检测内存泄漏
        sContext = getApplicationContext();
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
