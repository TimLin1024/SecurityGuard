package com.android.rdc.mobilesafe.entity;

import android.graphics.drawable.Drawable;

public class ScanAppInfo {

    private String mAppName;//应用名
    private Drawable mIcon;//图标
    private boolean mIsVirus;
    private String mPackageName;
    private String mDescription;

    public String getAppName() {
        return mAppName;
    }

    public void setAppName(String appName) {
        mAppName = appName;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public void setIcon(Drawable icon) {
        mIcon = icon;
    }

    public boolean isVirus() {
        return mIsVirus;
    }

    public void setVirus(boolean virus) {
        mIsVirus = virus;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String packageName) {
        mPackageName = packageName;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }
}
