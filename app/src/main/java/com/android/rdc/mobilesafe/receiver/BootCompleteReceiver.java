package com.android.rdc.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.rdc.mobilesafe.service.TrafficMonitoringService;
import com.android.rdc.mobilesafe.util.SystemUtil;

public class BootCompleteReceiver extends BroadcastReceiver {
    private static final String TAG = "BootCompleteReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: 开机广播");
        if (!SystemUtil.isServiceRunning(context, TrafficMonitoringService.class.getCanonicalName())) {
            context.startService(new Intent(context, TrafficMonitoringService.class));
        }
    }
}
