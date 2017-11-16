package com.android.rdc.mobilesafe.service;

import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.android.rdc.mobilesafe.base.BaseAccessibilityService;

public class SafeAccessibilityService extends BaseAccessibilityService {
    private static final String TAG = "SafeAccessibilityServic";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "onAccessibilityEvent: " + event.toString());
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
                event.getPackageName().equals("com.android.settings")) {
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
    public void onInterrupt() {

    }
}
