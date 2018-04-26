package com.android.rdc.mobilesafe.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Debug;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.bean.TaskInfo;

import java.util.ArrayList;
import java.util.List;

public final class TaskInfoParser {
    private TaskInfoParser() {
        throw new UnsupportedOperationException("u can't instantiate me");
    }

    /**
     * 获取所有正在运行的进程
     */
    public static List<TaskInfo> parseTaskInfo(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = am.getRunningAppProcesses();
        List<TaskInfo> taskInfoList = new ArrayList<>(32);
        for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcessInfoList) {
            TaskInfo taskInfo = new TaskInfo();
            String packageName = processInfo.processName;
            taskInfo.setPackageName(packageName);
            Debug.MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(new int[]{processInfo.pid});
            long memSize = memoryInfo[0].getTotalPrivateDirty() * 1024;
            taskInfo.setMemSize(memSize);
            try {
                PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
                taskInfo.setIcon(packageInfo.applicationInfo.loadIcon(pm));
                taskInfo.setAppName(packageInfo.applicationInfo.loadLabel(pm).toString());
                if ((ApplicationInfo.FLAG_SYSTEM & packageInfo.applicationInfo.flags) != 0) {
                    taskInfo.setUserApp(false);
                } else {
                    taskInfo.setUserApp(true);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                taskInfo.setAppName(packageName);
                //出现异常，使用默认图标
                taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_default));
            }
            taskInfoList.add(taskInfo);
        }
        return taskInfoList;
    }

}
