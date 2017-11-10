package com.android.rdc.mobilesafe.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseToolBarActivity;
import com.android.rdc.mobilesafe.constant.Constant;
import com.android.rdc.mobilesafe.receiver.SmsDatabaseChaneObserver;
import com.android.rdc.mobilesafe.util.SystemUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class TrafficMonitoringActivity extends BaseToolBarActivity {
    private static final String TAG = "TrafficMonitoringActivi";
    private static final int PERMISSIONS_REQUEST_SEND_SMS = 101;
    private static final int PERMISSIONS_REQUEST_RECEIVE_SMS = 102;

    @BindView(R.id.tv_traffic_remind)
    TextView mTvTrafficRemind;
    @BindView(R.id.tv_today_traffic)
    TextView mTvTodayTraffic;
    @BindView(R.id.tv_month_used_traffic)
    TextView mTvMonthUsedTraffic;
    @BindView(R.id.tv_month_total_traffic)
    TextView mTvMonthTotalTraffic;
    @BindView(R.id.btn_correct_flow)
    Button mBtnCorrectFlow;
    @BindView(R.id.constraint_layout)
    ConstraintLayout mConstraintLayout;

    private SharedPreferences mSp;
    private static final String KEY_TOTAL_FLOW = "TOTAL_FLOW";
    private static final String KEY_USED_FLOW = "USED_FLOW";
    private CorrectFlowReceiver mReceiver;
    //主要要完成 信息的获取，以及更新，首先要能够拿到短信（获取到手机号码），
    // 然后再进行处理（联通的已经完成），最后显示出来（界面待修改）
    // TODO: 2017/11/9 0009 1. 手机卡换位置（宿舍带取卡针） 2. 修改界面
    @Override
    protected int setResId() {
        return R.layout.activity_traffic_monitoring;
    }

    @Override
    protected void initData() {
        mSp = getSharedPreferences(Constant.SP_NAME_CONFIG, MODE_PRIVATE);
        boolean hasSetOperator = mSp.getBoolean(Constant.HAS_SET_OPERATOR, false);
        if (!hasSetOperator) {//如果还没有设置运营商，则先跳转到设置运营商界面
            startActivity(TrafficSettingActivity.class);
            finish();
        }
        // TODO: 2017/10/6 0006 开启流量监控服务
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, PERMISSIONS_REQUEST_RECEIVE_SMS);
            Log.d(TAG, "initData: 申请权限");
        } else {
            // 原来的敏感操作代码：发短信或者收短信
            Log.d(TAG, "initData: 注册广播");
            registerSmsReceiver();
            registerSmsDatabaseChangeObserver(getApplicationContext());
        }

    }

    @Override
    protected void onDestroy() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            Log.d(TAG, "onDestroy: 解除注册");
        }
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

    @OnClick(R.id.btn_correct_flow)
    public void onViewClicked() {
        if (!SystemUtil.hasSimCard()) {
            showToast("您的手机没有 SIM 卡，请先插入 SIM 卡，再进行查询");
            return;
        }

        //6.0 以上动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SEND_SMS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            sendMsgByMode();
        }

    }

    private void sendMsgByMode() {
        int i = mSp.getInt(Constant.KEY_CARD_OPERATOR, 0);
        SmsManager smsManager = SmsManager.getDefault();
        //根据运营商发送查询短信
        switch (i) {
            case 0:
                Snackbar.make(mConstraintLayout, "您还没有设置运营商，前往设置？", Snackbar.LENGTH_LONG)
                        .setAction("确定", v -> {
                            startActivity(TrafficSettingActivity.class);
                            TrafficMonitoringActivity.this.finish();
                        })
                        .show();
                break;
            case 1:
                //联通
                smsManager.sendTextMessage("10010", null, "CXLL", null, null);
                Log.d(TAG, "onViewClicked: " + "发送短信");
                break;
            case 2:
                //移动
                break;
            case 3:
                //电信
                break;
        }
    }

    private void registerSmsReceiver() {
        mReceiver = new CorrectFlowReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(1000);//设置高优先级别
        registerReceiver(mReceiver, filter);
    }

    public class CorrectFlowReceiver extends BroadcastReceiver {

        public CorrectFlowReceiver() {
            Log.d(TAG, "CorrectFlowReceiver: create");
        }

        /**
         * 只解析联通 4G
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: " + "接收到信息了");
            showToast("收到信息了");
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            for (Object obj : objs) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
                String body = smsMessage.getMessageBody();
                String address = smsMessage.getOriginatingAddress();

                Log.d(TAG, "onReceive: body:" + body);
                if (!address.equals("10010")) {
                    return;
                }
                long totalUsed = 0;
                long total = 0;
                long beyond = 0;

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

                mSp.edit()
                        .putLong(KEY_TOTAL_FLOW, total)
                        .putLong(KEY_USED_FLOW, totalUsed)
                        .apply();
                mTvMonthTotalTraffic.setText(String.format("本月流量：%s", Formatter.formatFileSize(context, (total))));
                mTvMonthUsedTraffic.setText(String.format("本月已用：%s", Formatter.formatFileSize(context, totalUsed)));
            }
        }
    }

    /**
     * 将字符串转化成Float类型数据
     * 返回单位为 kb
     **/
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_SEND_SMS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendMsgByMode();
                } else {
                    Snackbar.make(mConstraintLayout, "需要发送短信权限才能校正流量,重试？", Snackbar.LENGTH_LONG)
                            .setAction("确定", v -> {
                                //重新申请权限
                                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SEND_SMS);
                            })
                            .show();
                }
                break;
            case PERMISSIONS_REQUEST_RECEIVE_SMS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    registerSmsReceiver();
                } else {
                    Snackbar.make(mConstraintLayout, "需要接收短信权限才能校正流量哦,重试？", Snackbar.LENGTH_LONG)
                            .setAction("确定", v -> {
                                //重新申请权限
                                requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS}, PERMISSIONS_REQUEST_RECEIVE_SMS);
                            })
                            .show();
                }

                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public static final Uri SMS_MESSAGE_URI = Uri.parse("content://sms");

    private static SmsDatabaseChaneObserver mSmsDBChangeObserver;

    private static void registerSmsDatabaseChangeObserver(Context context) {
        //因为，某些机型修改rom导致没有getContentResolver
        try {
            mSmsDBChangeObserver = new SmsDatabaseChaneObserver(context.getContentResolver(), new Handler());
            context.getContentResolver().registerContentObserver(SMS_MESSAGE_URI, true, mSmsDBChangeObserver);
        } catch (Throwable b) {
        }
    }

    private static void unregisterSmsDatabaseChangeObserver(ContextWrapper contextWrapper) {
        try {
            contextWrapper.getContentResolver().unregisterContentObserver(mSmsDBChangeObserver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
