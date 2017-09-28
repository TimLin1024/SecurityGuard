package com.android.rdc.mobilesafe.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseToolBarActivity;
import com.android.rdc.mobilesafe.service.AutoKillProcessService;
import com.android.rdc.mobilesafe.util.SystemUtil;

import butterknife.BindView;

public class ProcessCleanSettingActivity extends BaseToolBarActivity implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "ProcessCleanSettingActi";
    @BindView(R.id.switch_show_sys_process)
    Switch mSwitchShowSysProcess;
    @BindView(R.id.switch_auto_clean)
    Switch mSwitchAutoClean;

    private SharedPreferences mSharedPreferences;
    private static final String KEY_SHOW_SYS_PROCESS = "KEY_SHOW_SYS_PROCESS";
    private static final String SP_NAME = "show_sys_process";

    @Override
    protected int setResId() {
        return R.layout.activity_process_clean_setting;
    }

    @Override
    protected void initData() {
        boolean serviceRunning = SystemUtil.isServiceRunning(this, AutoKillProcessService.class.getCanonicalName());
        mSwitchAutoClean.setChecked(serviceRunning);
        mSharedPreferences = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        mSwitchShowSysProcess.setChecked(mSharedPreferences.getBoolean(KEY_SHOW_SYS_PROCESS, true));
    }

    @Override
    protected void initView() {
        setTitle("优化加速设置");
        mSwitchAutoClean.setOnCheckedChangeListener(this);
        mSwitchShowSysProcess.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initListener() {
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_auto_clean:
                Intent intent = new Intent(this, AutoKillProcessService.class);
                if (isChecked) {
                    startService(intent);
                    Log.d(TAG, "onCheckedChanged: ");
                } else {
                    stopService(intent);
                }
                break;
            case R.id.switch_show_sys_process:
                mSharedPreferences
                        .edit()
                        .putBoolean(KEY_SHOW_SYS_PROCESS, isChecked)
                        .apply();
                break;
        }
    }
}
