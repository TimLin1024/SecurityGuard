package com.android.rdc.mobilesafe.receiver;

/**
 * 将字符串转化成Float类型数据
 * 返回单位为 kb
 **/

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import com.android.rdc.mobilesafe.callback.CorrectFlowCallback;
import com.android.rdc.mobilesafe.constant.Constant;


public class CorrectFlowReceiver extends BroadcastReceiver {
    private static final String TAG = "CorrectFlowReceiver";
    private CorrectFlowCallback mCallback;

    public CorrectFlowReceiver() {
        Log.d(TAG, "CorrectFlowReceiver: create");
    }

    /**
     * 只解析联通 4G
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: " + "接收到信息了");
        if (intent.getExtras() == null) {
            return;
        }
        Object[] objs = (Object[]) intent.getExtras().get("pdus");
        if (objs == null) {
            return;
        }
        StringBuilder body = new StringBuilder();
        String address = "";
        for (Object obj : objs) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
            body.append(smsMessage.getMessageBody());
            address = smsMessage.getOriginatingAddress();

            Log.d(TAG, "onReceive: body:" + body);
        }

        if (!address.equals("10010")) {
            return;
        }
        long totalUsed = 0;
        long total = 0;
        long beyond = 0;
        Log.d(TAG, "onReceive: body:" + body);
        String str = body.substring(body.indexOf("：", 7));
        String[] strAry = str.split("；");
        for (String s : strAry) {
            if (s.contains("本月总流量已用")) {
                String usedFlow = s.substring(s.indexOf("已用") + 2);
                totalUsed = string2Float(usedFlow);
            } else if (s.contains("套餐外")) {
                String out = s.substring(s.indexOf("已用") + 2);
                System.out.println(out);
                beyond = string2Float(out);
            } else if (s.contains("本地流量已用") || s.contains("省内") || s.contains("承诺") || s.contains("定向")) {
                String used = s.substring(s.indexOf("已用") + 2, s.indexOf("，"));
                String left = s.substring(s.indexOf("剩余") + 2);
                total += string2Float(used) + string2Float(left);
            }
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.SP_NAME_CONFIG, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putLong(Constant.KEY_TOTAL_FLOW, total)
                .putLong(Constant.KEY_USED_FLOW, totalUsed)
                .putLong(Constant.KEY_LAST_UPDATE_TIME, System.currentTimeMillis())
                .apply();
        if (mCallback != null) {
            mCallback.onCorrect(total, totalUsed);
        }
    }

    private long string2Float(String str) {
        long flow = 0;
        if (!TextUtils.isEmpty(str)) {
            if (str.contains("KB")) {
                String[] split = str.split("KB");
                float m = Float.parseFloat(split[0]);
                flow = (long) (m * 1024);
            } else if (str.contains("MB")) {
                String[] split = str.split("MB");
                float m = Float.parseFloat(split[0]);
                flow = (long) (m * 1024 * 1024);
            } else if (str.contains("GB")) {
                String[] split = str.split("GB");
                float m = Float.parseFloat(split[0]);
                flow = (long) (m * 1024 * 1024 * 1024);
            }
        }
        return flow;
    }

    public void setCorrectCallback(CorrectFlowCallback callback) {
        mCallback = callback;
    }
}