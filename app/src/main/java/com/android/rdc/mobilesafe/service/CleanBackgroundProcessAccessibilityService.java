package com.android.rdc.mobilesafe.service;

import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.android.rdc.mobilesafe.base.BaseAccessibilityService;

public class CleanBackgroundProcessAccessibilityService extends BaseAccessibilityService {
    private static final String TAG = "CleanBackgroundProcessA";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "onAccessibilityEvent: ");
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                && event.getPackageName().equals("com.android.settings")) {
            Log.d(TAG, "onAccessibilityEvent: com.android.settings");
            CharSequence className = event.getClassName();
            if (className.equals("com.android.settings.applications.InstalledAppDetailsTop")) {
                AccessibilityNodeInfo info = findViewByText("强行停止");
                if (info.isEnabled()) {
                    performViewClick(info);
                } else {
                    performBackClick();
                }
            }
            if (className.equals("android.app.AlertDialog")) {
                clickTextViewByText("确定");
                performBackClick();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }
}
