package com.android.rdc.mobilesafe.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseToolBarActivity;
import com.android.rdc.mobilesafe.service.AppLockService;
import com.android.rdc.mobilesafe.util.SystemInfoUtils;
import com.android.rdc.mobilesafe.ui.widget.SettingView;

import butterknife.BindView;


public class SettingActivityBase extends BaseToolBarActivity implements SettingView.OnCheckStatusChanged {

    @BindView(R.id.sv_blacklist_set)
    SettingView mSvBlacklistSet;
    @BindView(R.id.sv_app_lock_set)
    SettingView mSvAppLockSet;

    private SharedPreferences mSharedPreferences;
    private boolean mIsAppLockServiceRunning;

    public static final String APP_LOCK_SERVICE = "com.android.rdc.ch09.service.AppLockService";

    private static final String KEY_APP_LOCK = "IS_APP_LOCK_ON";
    private static final String KEY_BLACKLIST = "_IS_BLACKLIST_ON";

    @Override
    protected void onStart() {
        mIsAppLockServiceRunning = SystemInfoUtils.isServiceRunning(this, APP_LOCK_SERVICE);
        mSvAppLockSet.setChecked(mIsAppLockServiceRunning);
        mSvBlacklistSet.setChecked(mSharedPreferences.getBoolean(KEY_BLACKLIST, false));

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
        mSvAppLockSet.setOnCheckStatusChanged(this);
        mSvBlacklistSet.setOnCheckStatusChanged(this);
    }


    @Override
    public void onCheckStatusChange(View v, boolean isChecked) {
        switch (v.getId()) {
            case R.id.sv_blacklist_set:
                saveStatus(KEY_BLACKLIST, isChecked);
                break;
            case R.id.sv_app_lock_set:
                setAppLock(isChecked);
                    break;
        }
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
}
