package com.android.rdc.mobilesafe.util;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public final class SystemInfoUtils {
    private SystemInfoUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static boolean isServiceRunning(Context context, String className) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos = activityManager.getRunningServices(200);
        for (ActivityManager.RunningServiceInfo info : infos) {
            String serviceName = info.service.getClassName();
            if (serviceName.equals(className)) {
                return true;
            }
        }
        return false;
    }
}
