package com.android.rdc.mobilesafe.ui;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.android.rdc.mobilesafe.HomeActivity;
import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseToolBarActivity;
import com.android.rdc.mobilesafe.constant.Constant;
import com.android.rdc.mobilesafe.service.AppLockService;
import com.android.rdc.mobilesafe.ui.widget.RoundRectDialog;
import com.android.rdc.mobilesafe.util.NotificationUtil;
import com.android.rdc.mobilesafe.util.SystemInfoUtils;

import butterknife.BindView;
import butterknife.OnClick;

import static com.android.rdc.mobilesafe.constant.Constant.KEY_APP_LOCK_SERVICE_ON;
import static com.android.rdc.mobilesafe.constant.Constant.KEY_BLACKLIST;
import static com.android.rdc.mobilesafe.constant.Constant.KEY_SHOW_NOTIFICATION;


public class SettingActivity extends BaseToolBarActivity implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "SettingActivity";
    @BindView(R.id.switch_intercept)
    Switch mSwitchIntercept;
    @BindView(R.id.switch_app_lock)
    Switch mSwitchAppLock;
    @BindView(R.id.switch_show_notification)
    Switch mSwitchShowNotification;

    private SharedPreferences mSharedPreferences;
    private boolean mIsAppLockServiceRunning;
    private NotificationCompat.Builder mBuilder;


    @Override
    protected void onStart() {
        mIsAppLockServiceRunning = SystemInfoUtils.isServiceRunning(this, Constant.KEY_APP_LOCK_SERVICE_ON);//判断服务是否开启
        mSwitchShowNotification.setChecked(mSharedPreferences.getBoolean(KEY_SHOW_NOTIFICATION, false));//是否在通知栏显示
        mSwitchAppLock.setChecked(mIsAppLockServiceRunning);//设置是否程序锁是否开启
        mSwitchIntercept.setChecked(mSharedPreferences.getBoolean(KEY_BLACKLIST, false));//设置骚扰拦截是否开启
        super.onStart();
    }

    @Override
    protected int setResId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initData() {
        mSharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
    }

    @Override
    protected void initView() {
        setTitle("设置");
    }

    @Override
    protected void initListener() {
        mSwitchAppLock.setOnCheckedChangeListener(this);
        mSwitchIntercept.setOnCheckedChangeListener(this);
        mSwitchShowNotification.setOnCheckedChangeListener(this);
    }


    private void setAppLock(boolean isChecked) {
        saveStatus(KEY_APP_LOCK_SERVICE_ON, isChecked);
        Intent intent = new Intent(this, AppLockService.class);
        if (isChecked) {
            if (!mIsAppLockServiceRunning) {
                showToast("设置开启服务");
                startService(intent);
            }
        } else {
            showToast("设置停止服务");
            stopService(intent);
        }
    }

    private void saveStatus(String keyName, boolean isChecked) {
        if (TextUtils.isEmpty(keyName)) {
            return;
        }
        mSharedPreferences
                .edit()
                .putBoolean(keyName, isChecked)
                .apply();
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_app_lock:
                setAppLock(isChecked);
                break;
            case R.id.switch_intercept:
                saveStatus(KEY_BLACKLIST, isChecked);
                break;
            case R.id.switch_show_notification:
                resolveShowNotification(isChecked);
                break;
        }
    }

    private void resolveShowNotification(boolean isChecked) {
        mSharedPreferences.edit().putBoolean(Constant.KEY_SHOW_NOTIFICATION, isChecked).apply();//存入配置信息中

        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);//使用 appContext 防止内存泄漏
        if (isChecked) {
            if (mBuilder == null) {
                mBuilder = NotificationUtil.buildNotification(this);
            }
            manager.notify("通知", Constant.NOTIFICATION_ID, mBuilder.build());
        } else {
            manager.cancel(Constant.NOTIFICATION_ID);//取消通知
        }
    }

    @OnClick({R.id.tv_traffic_setting, R.id.tv_quit_app})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_traffic_setting:
                startActivity(TrafficSettingActivity.class);
                break;
            case R.id.tv_quit_app:
                new RoundRectDialog.Builder(this)
                        .setCancelable(true)
                        .setTitle("温馨提示")
                        .setMsg("退出卫士，可能会受到木马病毒、骚扰电话的侵扰，并造成流量监控不准，现在卫士内存占用很小，建议不要退出哦")
                        .setPositiveButton("确定退出", (dialog, which) -> startActivity(HomeActivity.newIntent(SettingActivity.this, true)))
                        .setNegativeButton("暂不退出", null)
                        .show();
                break;
        }
    }
}
