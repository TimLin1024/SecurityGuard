package com.android.rdc.mobilesafe.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import com.android.rdc.mobilesafe.bean.AppInfo;

public final class ManagerSoftwareUtil {
    private ManagerSoftwareUtil() {
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
     * 打开应用设置界面
     */
    public static void settingAppDetail(Context context, String packageName) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);//
        intent.setData(Uri.parse("package:" + packageName));
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
        // TODO: 2017/11/15 0015 尝试通过 A api 卸载
        if (appInfo.getPackageName().equals(context.getPackageName())) {
            ToastUtil.showToast(context, "您没有权限卸载此应用");//不允许删除本应用
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DELETE);
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:" + appInfo.getPackageName()));
        context.startActivity(intent);
        //非系统应用
//        if (appInfo.isUserApp()) {
//            if (appInfo.getPackageName().equals(context.getPackageName())) {
//                ToastUtil.showToast(context, "您没有权限卸载此应用");//不允许删除本应用
//            }
//            Intent intent = new Intent();
//            intent.setAction(Intent.ACTION_DELETE);
//            intent.addCategory("android.intent.category.DEFAULT");
//            intent.setData(Uri.parse("package:" + appInfo.getPackageName()));
//            context.startActivity(intent);
//        } else {
//            /**
//             * 需要 root 权限，利用 linux 命令删除文件
//             */
////            if (!RootShell.isRootAvailable()) {
////                ToastUtil.showToast(context, "卸载系统应用需要 Root 权限");
////                return;
////            }
////            Shell shell = null;
////            try {
////                shell = RootShell.getShell(true);
////            Command command = new Command(1, 3000, "mount -o remount ,rw /system");
////            Command command2 = new Command(1, 3000, "rm -r " + appInfo.getApkPath());
////                Shell.runRootCommand(command);
////                Shell.runRootCommand(command2);
////            } catch (IOException e) {
////                e.printStackTrace();
////            } catch (TimeoutException e) {
////                e.printStackTrace();
////            } catch (RootDeniedException e) {
////                e.printStackTrace();
////            }
//            if (CheckRootUtil.isRooted()) {
//                CommandResult re = Shell.SU.run("mount -o remount ,rw /system", "rm -r " + appInfo.getApkPath());
//                ToastUtil.showToast(context, re.getStdout());
//            } else {
//                ToastUtil.showToast(context, "删除系统应用需要 Root 权限");
//            }
//        }
    }

}

