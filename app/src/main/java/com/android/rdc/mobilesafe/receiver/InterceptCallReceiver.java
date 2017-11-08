package com.android.rdc.mobilesafe.receiver;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.android.rdc.mobilesafe.constant.Constant;
import com.android.rdc.mobilesafe.dao.BlackNumberDao;

import java.lang.reflect.Method;

public class InterceptCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sp = context.getSharedPreferences(Constant.SP_NAME_CONFIG, Context.MODE_PRIVATE);
        boolean isBlackNumOn = sp.getBoolean(Constant.BLACK_NUM_ON, true);
        if (!isBlackNumOn) {
            //未开启黑名单拦截
            return;
        }

        BlackNumberDao dao = new BlackNumberDao(context);
        if (!intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            String mIncomingNumber;
            // 如果是来电
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Service.TELEPHONY_SERVICE);
            switch (telephonyManager.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:
                    mIncomingNumber = intent.getStringExtra("incoming_number");
                    int blackContactMode = dao.getBlackContactMode(mIncomingNumber);
                    if (blackContactMode == 1 || blackContactMode == 3) {
                        // 观察（另外一个应用程序数据库的变化）呼叫记录的变化，如果呼叫记录生成了，就把呼叫记录给删除掉。
                        Uri uri = Uri.parse("content://call_log/calls");
                        context.getContentResolver().registerContentObserver(
                                uri,
                                true,
                                new CallLogObserver(new Handler(), mIncomingNumber,
                                        context));
                        endCall(context);
                    }
                    break;
            }

        }
    }

    private class CallLogObserver extends ContentObserver {
        private String mIncomingNumber;
        private Context mContext;

        CallLogObserver(Handler handler, String incomingNumber,
                        Context context) {
            super(handler);
            this.mIncomingNumber = incomingNumber;
            this.mContext = context;
        }

        // 观察到数据库内容变化调用的方法
        @Override
        public void onChange(boolean selfChange) {
            Log.i("CallLogObserver", "呼叫记录数据库的内容变化了。");
            mContext.getContentResolver().unregisterContentObserver(this);
            deleteCallLog(mIncomingNumber, mContext);
            super.onChange(selfChange);
        }
    }

    /**
     * 清除呼叫记录
     *
     * @param incomingNumber
     */
    public void deleteCallLog(String incomingNumber, Context context) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse("content://call_log/calls");
        Cursor cursor = resolver.query(uri, new String[]{"_id"}, "number=?",
                new String[]{incomingNumber}, "_id desc limit 1");
        if (cursor != null && cursor.moveToNext()) {
            String id = cursor.getString(0);
            resolver.delete(uri, "_id=?", new String[]{id});
        }
    }

    /**
     * 挂断电话
     */
    /**
     * {@hide}
     */
    public void endCall(Context context) {
        try {

            @SuppressLint("PrivateApi") Class clazz = context.getClassLoader().loadClass("android.os.ServiceManager");
            Method method = clazz.getDeclaredMethod("getService", String.class);
            IBinder iBinder = (IBinder) method.invoke(null,
                    Context.TELEPHONY_SERVICE);
            ITelephony itelephony = ITelephony.Stub.asInterface(iBinder);
            itelephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
