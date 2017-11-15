package com.android.rdc.mobilesafe.constant;

public class Constant {
    /**
     * 配置信息
     */
    public static final String SP_NAME_CONFIG = "config";
    public static final String BLACK_NUM_ON = "BLACK_NUM_ON";

    public static final String URI_APP_LOCK_DB = "content://com.android.rdc.mobilesafe.applock";// 程序锁数据库 uri
    public static final String KEY_LOCK_PWD = "password";//密码
    public static final String KEY_EXTRA_PACKAGE_NAME = "packagename";//
    public static final String KEY_SHOW_NOTIFICATION = "SHOW_NOTIFICATION";//显示通知栏
    public static final String KEY_APP_LOCK_SERVICE_ON = "IS_APP_LOCK_ON";
    public static final String KEY_BLACKLIST = "IS_BLACKLIST_ON";
    public static final String APP_LOCK_SERVICE_NAME = "com.android.rdc.ch09.service.AppLockService";
    public static final int NOTIFICATION_ID = 101;//通知栏 id

    /**
     * 广播接收的 filter_action
     */
    public static final String FILTER_ACTION = "com.android.rdc.mobilesafe.applock";

    /**
     * 设置运营商界面
     */
    public static final String HAS_SET_OPERATOR = "HAS_SET_OPERATOR";//是否已经设置了运营商
    public static final String KEY_SMS_COMMAND = "SMS_COMMAND";
    public static final String KEY_COMMAND_RECEIVER = "SMS_COMMAND_RECEIVER";

    /**
     * 省、市、运营商选择界面
     */
    public static final String KEY_CARD_PROVINCE = "CARD_PROVINCE";
    public static final String KEY_CARD_CITY = "CARD_CITY";
    public static final String KEY_CARD_OPERATOR = "CARD_OPERATOR";
    public static final String KEY_CARD_BRAND = "CARD_BRAND";

    public static final String KEY_CARD_PROVINCE_INDEX = "CARD_PROVINCE_INDEX";
    public static final String KEY_CARD_CITY_INDEX = "CARD_CITY_INDEX";
    public static final String KEY_CARD_OPERATOR_INDEX = "CARD_OPERATOR_INDEX";
    public static final String KEY_CARD_BRAND_INDEX = "CARD_BRAND_INDEX";


}
