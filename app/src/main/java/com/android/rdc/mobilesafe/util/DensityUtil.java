package com.android.rdc.mobilesafe.util;

import android.content.Context;

public final class DensityUtil {
    private DensityUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static int density2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
