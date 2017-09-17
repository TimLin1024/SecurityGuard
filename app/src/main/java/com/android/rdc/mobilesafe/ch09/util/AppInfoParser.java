package com.android.rdc.mobilesafe.ch09.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.android.rdc.mobilesafe.ch09.entity.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class AppInfoParser {
    public static List<AppInfo> getAppInfos(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);
        List<AppInfo> appInfoList = new ArrayList<>();
        for (PackageInfo packageInfo : packageInfoList) {
            AppInfo appInfo = new AppInfo();
            appInfo.mIcon = packageInfo.applicationInfo.loadIcon(packageManager);
            appInfo.mAppName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            appInfo.mPackageName = packageInfo.packageName;
            appInfo.apkPath = packageInfo.applicationInfo.sourceDir;
            appInfoList.add(appInfo);
        }
        return appInfoList;
    }
}
