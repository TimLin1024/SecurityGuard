package com.android.rdc.mobilesafe.ui;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.Build;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseActivity;
import com.android.rdc.mobilesafe.constant.Constant;
import com.android.rdc.mobilesafe.receiver.CorrectFlowReceiver;
import com.android.rdc.mobilesafe.util.DateUtil;
import com.android.rdc.mobilesafe.util.SystemUtil;

import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

public class TrafficMonitoringActivity extends BaseActivity {
    private static final String TAG = "TrafficMonitoringActivi";
    private static final int PERMISSIONS_REQUEST_SEND_SMS = 101;
    private static final int PERMISSIONS_REQUEST_RECEIVE_SMS = 102;

    @BindView(R.id.constraint_layout)
    ConstraintLayout mConstraintLayout;
    @BindView(R.id.tv_current_left)
    TextView mTvCurrentLeft;
    @BindView(R.id.tv_unit_left)
    TextView mTvUnitLeft;
    @BindView(R.id.tv_hint_status)
    TextView mTvHintStatus;
    @BindView(R.id.tv_correct_now)
    TextView mTvCorrectNow;
    @BindView(R.id.tv_last_update_time)
    TextView mTvLastUpdateTime;
    @BindView(R.id.tv_today_traffic)
    TextView mTvTodayTraffic;
    @BindView(R.id.tv_today_hint)
    TextView mTvTodayHint;
    @BindView(R.id.tv_month_total_traffic)
    TextView mTvMonthTotalTraffic;
    @BindView(R.id.tv_month_used_traffic)
    TextView mTvMonthUsedTraffic;

    private SharedPreferences mSp;
    private CorrectFlowReceiver mCorrectFlowReceiver;

    //主要要完成 信息的获取，以及更新，首先要能够拿到短信（获取到手机号码），
    // 然后再进行处理（联通的已经完成），最后显示出来（界面待修改）
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
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, PERMISSIONS_REQUEST_RECEIVE_SMS);
            Log.d(TAG, "initData: 申请权限");
        } else {
            Log.d(TAG, "initData: 注册广播");
            registerSmsReceiver();
//            registerSmsDatabaseChangeObserver(getApplicationContext());
        }

    }

    @Override
    protected void onDestroy() {
        if (mCorrectFlowReceiver != null) {
            unregisterReceiver(mCorrectFlowReceiver);
            Log.d(TAG, "onDestroy: 解除注册");
        }
        super.onDestroy();
    }

    @Override
    protected void initView() {
        updateUi(mSp.getLong(Constant.KEY_TOTAL_FLOW, 1), mSp.getLong(Constant.KEY_USED_FLOW, 0));
    }

    @Override
    protected void initListener() {

    }

    @OnClick(R.id.tv_correct_now)
    public void onViewClicked() {
        if (!SystemUtil.hasSimCard()) {
            showToast("您的手机没有 SIM 卡，请先插入 SIM 卡，再进行查询");
            return;
        }

        //6.0 以上动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SEND_SMS);
        } else {
            sendMsgByMode();
        }
    }

    private void sendMsgByMode() {
        String i = mSp.getString(Constant.KEY_CARD_OPERATOR, "中国联通");//
        SmsManager smsManager = SmsManager.getDefault();
        //根据运营商发送查询短信
        switch (i) {
            case "中国联通":
                //联通
                smsManager.sendTextMessage("10010", null, "CXLL", null, null);
                Log.d(TAG, "onViewClicked: " + "发送短信");
                break;
            case "中国移动":
                //移动
                break;
            case "中国电信":
                //电信
                break;
            default:
                Snackbar.make(mConstraintLayout, "您还没有设置运营商，前往设置？", Snackbar.LENGTH_LONG)
                        .setAction("确定", v -> {
                            startActivity(TrafficSettingActivity.class);
                            TrafficMonitoringActivity.this.finish();
                        })
                        .show();
                break;
        }
    }

    private void registerSmsReceiver() {
        mCorrectFlowReceiver = new CorrectFlowReceiver();
        mCorrectFlowReceiver.setCorrectCallback(this::updateUi);

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(1000);//设置高优先级别
        registerReceiver(mCorrectFlowReceiver, filter);
    }

    private void updateUi(long total, long totalUsed) {
        //本月总流量
        mTvMonthTotalTraffic.setText(Formatter.formatFileSize(getApplicationContext(), (total)));
        //本月已经使用的流量
        mTvMonthUsedTraffic.setText(Formatter.formatFileSize(getApplicationContext(), totalUsed));
        //当前剩余总流量
        String str = Formatter.formatFileSize(getApplicationContext(), Math.abs(total - totalUsed));
        mTvCurrentLeft.setText(str.replaceAll("[^(0-9)]", ""));
        mTvUnitLeft.setText(str.replaceAll("[^(a-zA-Z)]", ""));//单位
        //提示性文字
        if (total >= totalUsed) {
            mTvHintStatus.setText("—— 日常剩余 ——");
        } else {
            mTvHintStatus.setText("—— 日常超出 ——");
        }
        //设置更新时间
        long lastUpdateTime = mSp.getLong(Constant.KEY_LAST_UPDATE_TIME, 0);
        if (lastUpdateTime == 0) {
            mTvLastUpdateTime.setText("从未更新");
        } else {
            Date date = new Date(lastUpdateTime);
            mTvLastUpdateTime.setText(String.format("%s校正", DateUtil.getDateDiff(date)));
        }
        updateTodayTraffic();
    }

    private void updateTodayTraffic() {
        //6.0+ 可以获取当天的流量使用情况
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //没有权限先申请
            if (!checkReadNetworkStatsPermission()) {
                requestReadNetworkStats();
            }
            getTodayTraffic();
        } else {
            mTvTodayHint.setText("自开机已用");
            mTvTodayTraffic.setText(Formatter.formatFileSize(getApplicationContext(),
                    TrafficStats.getMobileRxBytes() + TrafficStats.getMobileTxBytes()));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getTodayTraffic() {
        NetworkStatsManager statsManager = (NetworkStatsManager) getApplicationContext().getSystemService(NETWORK_STATS_SERVICE);
        long todayUsed = 0;
        try {
            NetworkStats networkStats = statsManager.querySummary(
                    ConnectivityManager.TYPE_MOBILE, "", DateUtil.getTodayStartTime(), DateUtil.getTodayEndTime());
            NetworkStats.Bucket bucket = new NetworkStats.Bucket();
            if (networkStats.hasNextBucket()) {
                networkStats.getNextBucket(bucket);
            }
            todayUsed = bucket.getRxBytes();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        //今天已经使用的流量
        mTvTodayTraffic.setText(Formatter.formatFileSize(getApplicationContext(), todayUsed));
    }

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

    /**
     * 是否有读取网络状态的权限
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean checkReadNetworkStatsPermission() {
        final AppOpsManager appOps = (AppOpsManager) getApplicationContext().getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (appOps != null) {
            mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    // 打开“有权查看使用情况的应用”页面
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void requestReadNetworkStats() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent);
    }

}
