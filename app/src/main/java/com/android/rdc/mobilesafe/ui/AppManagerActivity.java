package com.android.rdc.mobilesafe.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseRvAdapter;
import com.android.rdc.mobilesafe.base.BaseToolBarActivity;
import com.android.rdc.mobilesafe.ch04appmanager.adapter.AppManagerAdapter;
import com.android.rdc.mobilesafe.ch04appmanager.entity.AppInfo;
import com.android.rdc.mobilesafe.ch04appmanager.util.AppInfoParser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class AppManagerActivity extends BaseToolBarActivity {

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
    private AppManagerAdapter mAppManagerAdapter;

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
                    if (mAllAppInfoList != null) {
                        mAppManagerAdapter.setDataList(mAllAppInfoList);
                        mAppManagerAdapter.notifyDataSetChanged();
                    } else {
                        showToast("获取的所有应用为空");
                    }
                    break;
                case UPDATE_APP_SELECT_STATE:
                    mAppManagerAdapter.notifyDataSetChanged();
                    break;

            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected int setResId() {
        return R.layout.activity_app_manager;
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAllAppInfoList.addAll(AppInfoParser.getAppInfos(AppManagerActivity.this));
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

        mAppManagerAdapter = new AppManagerAdapter();
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRv.setAdapter(mAppManagerAdapter);
        mAppManagerAdapter.setOnRvItemClickListener(new BaseRvAdapter.OnRvItemClickListener() {
            @Override
            public void onClick(final int position) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AppInfo currentAppInfo = mAppManagerAdapter.getDataList().get(position);
                        boolean flag = currentAppInfo.isSelected();//先记住状态,「选中与否」
                        for (AppInfo info : mAllAppInfoList) {
                            info.setSelected(false);
                        }
                        if (flag) {//原来已经选中，则修改为未选中
                            currentAppInfo.setSelected(false);
                        } else {
                            currentAppInfo.setSelected(true);
                        }
                        mHandler.sendEmptyMessage(UPDATE_APP_SELECT_STATE);
                    }
                }).start();
            }
        });

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

    }

    private class UninstallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //刷新应用数据
            updateData();
        }
    }
}
