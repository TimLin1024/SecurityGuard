package com.android.rdc.mobilesafe.util;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public final class SystemUtil {
    private SystemUtil() {
        throw new IllegalStateException("u can't instantiate me");
    }

    /**
     * 判断一个服务是否正在运行
     */
    public static boolean isServiceRunning(Context context, String className) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfoList = am.getRunningServices(200);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServiceInfoList) {
            if (className.equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static long getTotalMem() {
        try {
            FileInputStream fis = new FileInputStream(new File("/proc/meminfo"));
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String totalInfo = br.readLine();//读取第一行—— MemTotal:    94096 kB
            StringBuilder stringBuilder = new StringBuilder();
            for (char c : totalInfo.toCharArray()) {
                if (c >= '0' && c <= '9') {
                    stringBuilder.append(c);
                }
            }
            return Long.parseLong(stringBuilder.toString()) * 1024;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long getTotalMemWithReflect() {
        Method readProclinesMethod = null;
        Class procClass;
        try {
            procClass = Class.forName("android.os.Process");
            Class parameterTypes[] = new Class[]{String.class, String[].class, long[].class};
            readProclinesMethod = procClass.getMethod("readProcLines", parameterTypes);
            Object argList[] = new Object[3];
            final String[] memInfoFields = new String[]{"MemTotal:",
                    "MemFree:", "Buffers:", "Cached:"};
            long[] memInfoSizes = new long[memInfoFields.length];
            memInfoSizes[0] = 30;
            memInfoSizes[1] = -30;
            argList[0] = "/proc/meminfo";
            argList[1] = memInfoFields;
            argList[2] = memInfoSizes;
            if (readProclinesMethod != null) {
                readProclinesMethod.invoke(null, argList);
                for (int i = 0; i < memInfoSizes.length; i++) {
                    Log.d("GetFreeMem", memInfoFields[i] + " : " + memInfoSizes[i] / 1024);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long getAvailableMem(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        return outInfo.availMem;
    }

    public static int getRunningProcessCount(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return am.getRunningAppProcesses().size();
    }


}
