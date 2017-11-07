package com.android.rdc.mobilesafe.bean;

import android.graphics.drawable.Drawable;

public class CacheInfo {

    private Drawable mIcon;
    private String mPackageName;
    private String mAppName;
    private long mCacheSize;//缓存大小
    private boolean isSelected = true;

    public Drawable getIcon() {
        return mIcon;
    }

    public void setIcon(Drawable icon) {
        mIcon = icon;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String packageName) {
        mPackageName = packageName;
    }

    public String getAppName() {
        return mAppName;
    }

    public void setAppName(String appName) {
        mAppName = appName;
    }

    public long getCacheSize() {
        return mCacheSize;
    }

    public void setCacheSize(long cacheSize) {
        mCacheSize = cacheSize;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
