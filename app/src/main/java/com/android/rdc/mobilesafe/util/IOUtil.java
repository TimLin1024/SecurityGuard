package com.android.rdc.mobilesafe.util;

import android.content.Context;

import java.io.Closeable;
import java.io.IOException;

public final class IOUtil {
    private IOUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void closeQuietly(Closeable closeable) {

        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class UIUtil {
        public static int getStatusBarHeight(Context context) {
            int result = 0;
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = context.getResources().getDimensionPixelSize(resourceId);
            }
            return result;
        }

    }
}
