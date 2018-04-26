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
        //这里遇到的问题是：需要挂断电话，但是，google 工程师出于安全的考虑，
        // 将 endCall 方法隐藏掉了。
        // 怎么解决？
        // ①就通过反射的形式去调用这个类的方法。
        // ②查看源码，看一看系统是如何实现该方法的，查看源码得知：是通过 ITelephony 这个接口实现的（一个 aidl 接口）
        //  因此将 ITelephony.aidl 文件(位于 internal 包中) copy 到相应目录下面。
        //  ITelephony.aidl 文件关联到另外一个 aidl 文件，导致出错
        BlackNumberDao dao = new BlackNumberDao(context);
        if (!Intent.ACTION_NEW_OUTGOING_CALL.equals(intent.getAction())) {
            String mIncomingNumber;
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Service.TELEPHONY_SERVICE);
            switch (telephonyManager.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:// 如果是来电
                    mIncomingNumber = intent.getStringExtra("incoming_number");//获取来电号码
                    int blackContactMode = dao.getBlackContactMode(mIncomingNumber);
                    if (blackContactMode == 1 || blackContactMode == 3) {
                        // 观察（另外一个应用程序数据库的变化）呼叫记录的变化，
                        // 如果呼叫记录生成了，就把呼叫记录给删除掉。
                        Uri uri = Uri.parse("content://call_log/calls");
                        context.getContentResolver().registerContentObserver(
                                uri,
                                true,
                                new CallLogObserver(new Handler(), mIncomingNumber,
                                        context)
                        );
                        endCall(context);//终止通话
                    }
                    break;
            }
        }
    }

    private class CallLogObserver extends ContentObserver {
        private String mIncomingNumber;
        private Context mContext;

        CallLogObserver(Handler handler, String incomingNumber, Context context) {
            super(handler);
            this.mIncomingNumber = incomingNumber;
            this.mContext = context;
        }

        // 观察到数据库内容变化调用的方法
        @Override
        public void onChange(boolean selfChange) {
            Log.i("CallLogObserver", "呼叫记录数据库的内容变化了。");
            mContext.getContentResolver().unregisterContentObserver(this);
            deleteCallLog(mIncomingNumber, mContext);//删除记录
            super.onChange(selfChange);
        }
    }

    /**
     * 清除呼叫记录
     *
     * @param incomingNumber 来电号码
     */
    public void deleteCallLog(String incomingNumber, Context context) {
        ContentResolver resolver = context.getContentResolver();//获取 ContentResolver
        Uri uri = Uri.parse("content://call_log/calls");
        Cursor cursor = resolver.query(uri, new String[]{"_id"}, "number=?",
                new String[]{incomingNumber}, "_id desc limit 1");
        if (cursor != null && cursor.moveToNext()) {
            String id = cursor.getString(0);
            resolver.delete(uri, "_id=?", new String[]{id});
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    /**
     * 挂断电话,因为源码中使用 @hide 修饰了 android.telephony.TelephonyManager#endCall 方法，
     * 所以需要通过反射获取到类，然后使用 Context.TELEPHONY_SERVICE 将它们
     */
    public void endCall(Context context) {
        try {
            @SuppressLint("PrivateApi") Class clazz = context.getClassLoader().loadClass("android.os.ServiceManager");//通过类加载器加载 ServiceManager
            Method method = clazz.getDeclaredMethod("getService", String.class);//获取声明的方法
            IBinder iBinder = (IBinder) method.invoke(null,
                    Context.TELEPHONY_SERVICE);//获取通话服务，这里拿到的是一个 IBinder 对象
            ITelephony itelephony = ITelephony.Stub.asInterface(iBinder);// 虽然 Binder 实体仅有一个，但是 Binder 的「影子」存在于多个进程中
            itelephony.endCall();//结束通话
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
