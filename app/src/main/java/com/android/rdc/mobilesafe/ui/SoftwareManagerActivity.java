package com.android.rdc.mobilesafe.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.adapter.SoftwareManagerAdapter;
import com.android.rdc.mobilesafe.base.BaseToolBarActivity;
import com.android.rdc.mobilesafe.entity.AppInfo;
import com.android.rdc.mobilesafe.util.AppInfoParser;
import com.android.rdc.mobilesafe.util.ProgressDialogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 软件管理
 */
public class SoftwareManagerActivity extends BaseToolBarActivity {

    @BindView(R.id.tv_phone_mem)
    TextView mTvPhoneMem;
    @BindView(R.id.tv_sd_card)
    TextView mTvSdCard;
    @BindView(R.id.rv)
    RecyclerView mRv;



    private List<AppInfo> mSystemAppInfoList = new ArrayList<>(32);
    private List<AppInfo> mUserAppInfoList = new ArrayList<>(32);

    private List<AppInfo> mAllAppInfoList;

    private UninstallReceiver mUninstallReceiver;
    private SoftwareManagerAdapter mSoftwareManagerAdapter;
//    private AppDeleteReceiver appDeleteReceiver;

    public static final int REMOVE_APP_SUCCESS = 1;
    public static final int FETCH_APP_SUCCESS = 2;
    public static final int UPDATE_APP_SELECT_STATE = 3;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REMOVE_APP_SUCCESS:
                    break;
                case FETCH_APP_SUCCESS:
                    ProgressDialogUtil.dismiss();
                    if (mAllAppInfoList != null) {
                        mSoftwareManagerAdapter.setDataList(mAllAppInfoList);
                        mSoftwareManagerAdapter.notifyDataSetChanged();
                    } else {
                        showToast("获取的所有应用为空");
                    }
                    break;
                case UPDATE_APP_SELECT_STATE:
                    mSoftwareManagerAdapter.notifyDataSetChanged();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected int setResId() {
        return R.layout.activity_software_manager;
    }

    @Override
    protected void initData() {
        fetchAppInfoAsync();
    }

    /**
     * 获取数据
     */
    private void fetchAppInfoAsync() {
        mAllAppInfoList = new ArrayList<>(64);
        ProgressDialogUtil.showDefaultDialog(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAllAppInfoList.addAll(AppInfoParser.getAppInfos(SoftwareManagerActivity.this));
                mSystemAppInfoList.clear();
                mUserAppInfoList.clear();

                for (AppInfo appInfo : mAllAppInfoList) {
                    if (appInfo.isUserApp()) {
                        mUserAppInfoList.add(appInfo);
                    } else {
                        mSystemAppInfoList.add(appInfo);
                    }
                }
                mHandler.sendEmptyMessage(FETCH_APP_SUCCESS);
            }
        }).start();
    }

    @Override
    protected void initView() {
        setTitle("软件管理");

    }

    @Override
    protected void initListener() {
        mUninstallReceiver = new UninstallReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        registerReceiver(mUninstallReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        //释放监听器
        unregisterReceiver(mUninstallReceiver);
        super.onDestroy();
    }

    //刷新应用数据
    private void updateData() {
        fetchAppInfoAsync();
    }

    private class UninstallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            showToast("应用卸载" + intent.getData().getSchemeSpecificPart());
            //刷新应用数据
            updateData();
        }
    }
}
