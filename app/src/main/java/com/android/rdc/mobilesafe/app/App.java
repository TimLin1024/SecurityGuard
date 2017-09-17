package com.android.rdc.mobilesafe.app;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.tencent.bugly.crashreport.CrashReport;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        CrashReport.initCrashReport(getApplicationContext());
    }
}
