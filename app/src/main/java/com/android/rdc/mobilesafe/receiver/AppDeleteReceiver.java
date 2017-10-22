package com.android.rdc.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.rdc.mobilesafe.util.ToastUtil;

public class AppDeleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_FULLY_REMOVED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            ToastUtil.showToast(context, "应用卸载了" + packageName);
        }

    }
}
