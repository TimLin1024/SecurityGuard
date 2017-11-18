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


    /**
     * pattern yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getDateDiff(Date date/*, String pattern*/) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        String dateStr = format(date, pattern);
        String year = dateStr.substring(0, 4);
        Long yearNum = Long.parseLong(year);
        int month = Integer.parseInt(dateStr.substring(5, 7));
        int day = Integer.parseInt(dateStr.substring(8, 10));
        String hour = dateStr.substring(11, 13);
        String minute = dateStr.substring(14, 16);

        long addtime = date.getTime();

        long today = System.currentTimeMillis();//当前时间的毫秒数
        Date now = new Date(today);
        String nowStr = format(now, pattern);
        int nowDay = Integer.parseInt(nowStr.substring(8, 10));
        String result;
        long intervalTime = today - addtime;//当前时间与给定时间差的毫秒数
        long days = intervalTime / (24 * 60 * 60 * 1000);//这个时间相差的天数整数，大于1天为前天的时间了，小于24小时则为昨天和今天的时间
        long hours = (intervalTime / (60 * 60 * 1000) - days * 24);//这个时间相差的减去天数的小时数
        long min = ((intervalTime / (60 * 1000)) - days * 24 * 60 - hours * 60);//
        long s = (intervalTime / 1000 - days * 24 * 60 * 60 - hours * 60 * 60 - min * 60);
        if (days > 0) {
            if (days > 0 && days < 2) {
                result = "前天" + hour + "点" + minute + "分";
            } else {
                result = /*yearNum % 100 + "年"*/ +month + "月 " + day + "日" + hour + "点" + minute + "分";
            }
        } else if (hours > 0) {
            if (day != nowDay) {
                result = "昨天" + hour + "点" + minute + "分";
            } else {
                result = hours + "小时 前";
            }
        } else if (min > 0) {
            if (min > 0 && min < 15) {
                result = "刚刚";
            } else {
                result = min + "分 前";
            }
        } else {
            result = s + "秒 前";
        }
        return result;
    }

    /**
     * 日期格式化
     *
     * @param date    需要格式化的日期
     * @param pattern 时间格式，如：yyyy-MM-dd HH:mm:ss
     * @return 返回格式化后的时间字符串
     */
    public static String format(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

}
