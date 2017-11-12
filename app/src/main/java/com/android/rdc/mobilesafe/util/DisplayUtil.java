package com.android.rdc.mobilesafe.util;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.view.WindowManager;

public final class DisplayUtil {
    private DisplayUtil() {
    }

    @NonNull
    public static Point getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Point screenSizePoint = new Point();
        wm.getDefaultDisplay().getSize(screenSizePoint);
        return screenSizePoint;
    }
}
