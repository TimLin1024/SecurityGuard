package com.android.rdc.mobilesafe.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import com.android.rdc.mobilesafe.entity.AppInfo;

public final class EngineUtils {
    private EngineUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 分享应用
     */
    public static void shareApp(Context context, AppInfo appInfo) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "推荐您使用一款软件，名称叫：" + appInfo.getName()
                + "下载路径：https://play.google.com/store/apps/details?id=?"
                + appInfo.getPackageName());
        context.startActivity(intent);
    }

    /**
     * 打开应用设置面
     */
    public static void settingAppDetail(Context context, AppInfo appInfo) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + appInfo.getPackageName()));
        context.startActivity(intent);
    }

    /**
     * 启动应用
     */
    public static void startApp(Context context, AppInfo appInfo) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(appInfo.getPackageName());
        if (intent != null) {
            context.startActivity(intent);
        } else {
            ToastUtil.showToast(context, "该应用没有界面");
        }
    }

    /**
     * 卸载应用
     */
    public static void uninstallApp(Context context, AppInfo appInfo) {
        //非系统应用
        if (!appInfo.isUserApp()) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + appInfo.getPackageName()));
            context.startActivity(intent);
        } else {
            // TODO: 2017/9/17 0017 删除系统应用
            /**
             * 需要 root 权限，利用 linux 命令删除文件
             * */

        }
    }

}

