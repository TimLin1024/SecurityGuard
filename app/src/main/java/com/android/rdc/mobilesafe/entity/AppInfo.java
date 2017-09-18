package com.android.rdc.mobilesafe.entity;

import android.graphics.drawable.Drawable;

public class AppInfo {

    private String mApkPath;//安装路径
    private String mPackageName;//包名
    private String mName;//应用名
    private Drawable mIcon;//图标
    private long mAppSize;//app 大小
    private boolean mIsInRom;//是否安装在内存中

    private boolean isUserApp;//是否是用户应用
    private boolean isSelected;//是否被选中


    public String getAppLocation(boolean isInRom) {
        return isInRom ? "手机内存" : "外部存储卡";
    }

    public String getApkPath() {
        return mApkPath;
    }

    public void setApkPath(String apkPath) {
        mApkPath = apkPath;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String packageName) {
        mPackageName = packageName;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public void setIcon(Drawable icon) {
        mIcon = icon;
    }

    public long getAppSize() {
        return mAppSize;
    }

    public void setAppSize(long appSize) {
        mAppSize = appSize;
    }


    public boolean isUserApp() {
        return isUserApp;
    }

    public void setUserApp(boolean userApp) {
        isUserApp = userApp;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public boolean isInRom() {
        return mIsInRom;
    }

    public void setIsInRom(boolean inRom) {
        mIsInRom = inRom;
    }

}
