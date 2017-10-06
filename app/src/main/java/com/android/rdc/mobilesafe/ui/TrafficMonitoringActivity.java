package com.android.rdc.mobilesafe.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseToolBarActivity;
import com.android.rdc.mobilesafe.constant.Constant;
import com.android.rdc.mobilesafe.util.SystemUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class TrafficMonitoringActivity extends BaseToolBarActivity {

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

    @Override
    protected int setResId() {
        return R.layout.activity_traffic_monitoring;
    }

    @Override
    protected void initData() {
        mSp = getSharedPreferences(Constant.CONFIG, MODE_PRIVATE);
        boolean hasSetOperator = mSp.getBoolean(Constant.HAS_SET_OPERATOR, false);
        if (!hasSetOperator) {//如果还没有设置运营商，则先跳转到设置运营商界面
            startActivity(OperatorSettingActivity.class);
            finish();
        }
        // TODO: 2017/10/6 0006 开启流量监控服务

        registerSmsReceiver();
    }

    private void registerSmsReceiver() {
        mReceiver = new CorrectFlowReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);//设置高优先级别
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
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
        int i = mSp.getInt(Constant.KEY_OPERATOR, 0);
        // TODO: 2017/10/6 0006 临时测试
        SmsManager smsManager = SmsManager.getDefault();

        if (!SystemUtil.hasSimCard()) {
            showToast("您的手机没有 SIM 卡，请先插入 SIM 卡，再进行查询");
            return;
        }

        //根据运营商发送查询短信
        switch (i) {
            case 0:
                Snackbar.make(mConstraintLayout, "您还没有设置运营商，前往设置？", Snackbar.LENGTH_LONG)
                        .setAction("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(OperatorSettingActivity.class);
                                TrafficMonitoringActivity.this.finish();
                            }
                        })
                        .show();
                break;
            case 1:
                //移动
                break;
            case 2:
                //联通
                smsManager.sendTextMessage("10010", null, "CXLL", null, null);
                break;
            case 3:
                //电信
                break;
        }
    }


    class CorrectFlowReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            for (Object obj : objs) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
                String body = smsMessage.getMessageBody();
                String address = smsMessage.getOriginatingAddress();
                if (!address.equals("10010")) {
                    return;
                }
                String[] splits = body.split(",");
                long left = 0;
                long used = 0;
                long beyond = 0;
                for (String split : splits) {
                    if (split.contains("本月总流量已用")) {
                        String usedFlow = split.substring(7, split.length());
                        used = string2Float(usedFlow);
                    } else if (split.contains("剩余")) {
                        String leftFlow = split.substring(4, split.length());
                        left = string2Float(leftFlow);
                    } else if (split.contains("套餐外收费流量已用")) {
                        String beyondFlow = split.substring(5, split.length());
                        beyond = string2Float(beyondFlow);
                    }
                }
                mSp.edit()
                        .putLong(KEY_TOTAL_FLOW, used + left)
                        .putLong(KEY_USED_FLOW, used)
                        .apply();
                mTvMonthTotalTraffic.setText("本月流量：" + Formatter.formatFileSize(context, (used + left)));
                mTvMonthUsedTraffic.setText("本月已用：" + Formatter.formatFileSize(context, used));

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

}
