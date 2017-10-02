package com.android.rdc.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsMessage;

import com.android.rdc.mobilesafe.constant.Constant;
import com.android.rdc.mobilesafe.dao.BlackNumberDao;

public class InterceptSmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sp = context.getSharedPreferences(Constant.CONFIG, Context.MODE_PRIVATE);
        boolean isBlackNumOn = sp.getBoolean(Constant.BLACK_NUM_ON, true);
        if (!isBlackNumOn) {
            //未开启黑名单拦截
            return;
        }
        BlackNumberDao dao = new BlackNumberDao(context);
        Object[] objs = (Object[]) intent.getExtras().get("pdus");
        for (Object obj : objs) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
            String sender = smsMessage.getOriginatingAddress();
            String body = smsMessage.getMessageBody();
            if (sender.startsWith("+86")) {
                sender = sender.substring(3, sender.length());
            }
            int mode = dao.getBlackContactMode(sender);
            if (mode == 2 || mode == 3) {
                //阻止广播传递
                abortBroadcast();
            }

        }

    }
}
