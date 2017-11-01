package com.android.rdc.mobilesafe.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseToolBarActivity;
import com.android.rdc.mobilesafe.service.AppLockService;
import com.android.rdc.mobilesafe.util.SystemInfoUtils;

import butterknife.BindView;


public class SettingActivity extends BaseToolBarActivity implements CompoundButton.OnCheckedChangeListener {

//    @BindView(R.id.sv_blacklist_set)
//    SettingView mSvBlacklistSet;
//    @BindView(R.id.sv_app_lock_set)
//    SettingView mSvAppLockSet;

    @BindView(R.id.switch_intercept)
    Switch mSwitchIntercept;
    @BindView(R.id.switch_app_lock)
    Switch mSwitchAppLock;

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
        setTitle("设置中心");
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
}
