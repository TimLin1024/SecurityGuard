package com.android.rdc.mobilesafe.bean;

import android.graphics.drawable.Drawable;

public class TaskInfo {

    private Drawable mIcon;
    private String mPackageName;
    private String mAppName;
    private long mMemSize;//占用的内存
    private boolean mIsUserApp;
    private boolean mIsChecked;

    public Drawable getIcon() {
        return mIcon;
    }

    public void setIcon(Drawable icon) {
        mIcon = icon;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String name) {
        mPackageName = name;
    }

    public long getMemSize() {
        return mMemSize;
    }

    public void setMemSize(long memSize) {
        mMemSize = memSize;
    }

    public boolean isUserApp() {
        return mIsUserApp;
    }

    public void setUserApp(boolean userApp) {
        mIsUserApp = userApp;
    }

    public String getAppName() {
        return mAppName;
    }

    public void setAppName(String appName) {
        mAppName = appName;
    }

    public boolean isChecked() {
        return mIsChecked;
    }

    public void setChecked(boolean checked) {
        mIsChecked = checked;
    }
}
