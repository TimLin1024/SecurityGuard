package com.android.rdc.mobilesafe.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.android.rdc.mobilesafe.HomeActivity;
import com.android.rdc.mobilesafe.R;

public final class NotificationUtil {
    private NotificationUtil() {
    }

    public static NotificationCompat.Builder buildNotification(Context context) {
        Intent resultIntent = new Intent(context, HomeActivity.class);
        // 创建返回栈
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // 添加 Activity 到返回栈
        stackBuilder.addParentStack(HomeActivity.class);
        // 添加 Intent 到栈顶
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle("手机管家")
                .setContentText("")
                .setSmallIcon(R.drawable.ic_launcher)
                .setPriority(Notification.PRIORITY_MAX)
                .setShowWhen(true)
                .setContentIntent(resultPendingIntent);
        return builder;
    }
}
