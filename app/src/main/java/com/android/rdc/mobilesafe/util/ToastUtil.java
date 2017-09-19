package com.android.rdc.mobilesafe.util;

import android.content.Context;
import android.widget.Toast;

public final class ToastUtil {
    private ToastUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
