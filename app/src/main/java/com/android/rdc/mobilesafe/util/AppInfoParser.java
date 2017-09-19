package com.android.rdc.mobilesafe.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.android.rdc.mobilesafe.entity.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class AppInfoParser {
    private AppInfoParser() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static List<AppInfo> getAppInfos(Context context) {
        PackageManager packageManager = context.getPackageManager();//获取 pms
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);//获取安装的应用
        List<AppInfo> appInfoList = new ArrayList<>(32);

        for (PackageInfo packageInfo : packageInfoList) {
            AppInfo appInfo = new AppInfo();
            appInfo.setName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
            appInfo.setPackageName(packageInfo.packageName);
            appInfo.setIcon(packageInfo.applicationInfo.loadIcon(packageManager));
            appInfo.setApkPath(packageInfo.applicationInfo.sourceDir);
            File file = new File(appInfo.getApkPath());
            appInfo.setAppSize(file.length());

            int flags = packageInfo.applicationInfo.flags;//二进制映射
            /*是否存在内存中*/
            if ((ApplicationInfo.FLAG_EXTERNAL_STORAGE & flags) != 0) {
                appInfo.setIsInRom(false);
            } else {
                appInfo.setIsInRom(true);
            }
            /*是否是系统应用*/
            if ((ApplicationInfo.FLAG_SYSTEM & flags) != 0) {
                appInfo.setUserApp(false);
            } else {
                appInfo.setUserApp(true);
            }
            appInfoList.add(appInfo);
        }
        return appInfoList;
    }
}
