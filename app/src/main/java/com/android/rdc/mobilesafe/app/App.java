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
        Stetho.initializeWithDefaults(this);
        CrashReport.initCrashReport(getApplicationContext());

        sContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return sContext;
    }
}
