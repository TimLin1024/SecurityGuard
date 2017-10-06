package com.android.rdc.mobilesafe.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtil {
    private DateUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    private static SimpleDateFormat sSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static String formatDate(Date date) {
        return sSimpleDateFormat.format(date);
    }

}
