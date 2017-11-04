package com.android.rdc.mobilesafe.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.android.rdc.mobilesafe.HomeActivity;
import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseToolBarActivity;
import com.android.rdc.mobilesafe.service.AppLockService;
import com.android.rdc.mobilesafe.util.SystemInfoUtils;

import butterknife.BindView;
import butterknife.OnClick;


public class SettingActivity extends BaseToolBarActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.switch_intercept)
    Switch mSwitchIntercept;
    @BindView(R.id.switch_app_lock)
    Switch mSwitchAppLock;
    @BindView(R.id.switch_show_notification)
    Switch mSwitchShowNotification;
    @BindView(R.id.tv_quit_app)
    TextView mTvQuitApp;

    private SharedPreferences mSharedPreferences;
    private boolean mIsAppLockServiceRunning;

    public static final String APP_LOCK_SERVICE = "com.android.rdc.ch09.service.AppLockService";
    private static final String KEY_APP_LOCK = "IS_APP_LOCK_ON";
    private static final String KEY_BLACKLIST = "_IS_BLACKLIST_ON";

    @Override
    protected void onStart() {
        mIsAppLockServiceRunning = SystemInfoUtils.isServiceRunning(this, APP_LOCK_SERVICE);//判断服务是否开启
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
    }


    private void setAppLock(boolean isChecked) {
        saveStatus(KEY_APP_LOCK, isChecked);
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
        }
    }

    @OnClick(R.id.tv_quit_app)
    public void onViewClicked() {
        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle("温馨提示")
                .setMessage("退出卫士，可能会受到木马病毒、骚扰电话的侵扰，并造成流量监控不准，现在卫士内存占用很小，建议不要退出哦")
                .setPositiveButton("确定退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(HomeActivity.newIntent(SettingActivity.this, true));
                    }
                })
                .setNegativeButton("暂不退出", null)
                .show();

    }
}
