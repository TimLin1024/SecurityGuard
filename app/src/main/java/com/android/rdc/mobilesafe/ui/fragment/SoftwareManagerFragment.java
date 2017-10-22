package com.android.rdc.mobilesafe.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.adapter.SoftwareManagerAdapter;
import com.android.rdc.mobilesafe.base.BaseFragment;
import com.android.rdc.mobilesafe.base.BaseRvAdapter;
import com.android.rdc.mobilesafe.entity.AppInfo;
import com.android.rdc.mobilesafe.util.AppInfoParser;
import com.android.rdc.mobilesafe.util.ProgressDialogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;

public class SoftwareManagerFragment extends BaseFragment {

    private static final String TAG = "SoftwareManagerFragment";
    public static final int REMOVE_APP_SUCCESS = 101;
    public static final int FETCH_APP_SUCCESS = 102;
    public static final int UPDATE_APP_SELECT_STATE = 103;

    public static final int DISPLAY_USER_APP = 0;
    public static final int DISPLAY_SYSTEM_APP = 1;
    private static final String KEY_DISPLAY_TYPE = "DISPLAY_TYPE";
    private int mDisplayType;
    private String mLastDeletePackage;//上一个被删除的应用的包名
    private ExecutorService mExecutorService;


    @BindView(R.id.rv)
    RecyclerView mRv;

    private List<AppInfo> mSystemAppInfoList = new ArrayList<>(32);
    private List<AppInfo> mUserAppInfoList = new ArrayList<>(32);
    private List<AppInfo> mAllAppInfoList = new ArrayList<>(64);
    private UninstallReceiver mUninstallReceiver;
    private SoftwareManagerAdapter mSoftwareManagerAdapter;


    public static SoftwareManagerFragment newInstance(int displayType) {
        Bundle args = new Bundle();
        args.putInt(KEY_DISPLAY_TYPE, displayType);
        SoftwareManagerFragment fragment = new SoftwareManagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ProgressDialogUtil.dismiss();
            switch (msg.what) {
                case REMOVE_APP_SUCCESS:
                    mSoftwareManagerAdapter.notifyDataSetChanged();
                    break;
                case FETCH_APP_SUCCESS:
                    switch (mDisplayType) {
                        case DISPLAY_USER_APP:
                            mSoftwareManagerAdapter.setDataList(mUserAppInfoList);
                            break;
                        case DISPLAY_SYSTEM_APP:
                            mSoftwareManagerAdapter.setDataList(mSystemAppInfoList);
                            break;
                    }
                    mSoftwareManagerAdapter.notifyDataSetChanged();
                    break;
                case UPDATE_APP_SELECT_STATE:
                    mSoftwareManagerAdapter.notifyDataSetChanged();
                    break;
                default:
            }
        }
    };


    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_software_manager;
    }

    @Override
    protected void initData(Bundle bundle) {
        mExecutorService = Executors.newFixedThreadPool(5);
        mDisplayType = bundle.getInt(KEY_DISPLAY_TYPE);//显示用户应用还是系统应用
        fetchAppInfoAsync(FETCH_APP_SUCCESS);

        mSoftwareManagerAdapter = new SoftwareManagerAdapter();
        mRv.setLayoutManager(new LinearLayoutManager(mBaseActivity));
        mRv.addItemDecoration(new DividerItemDecoration(mBaseActivity, DividerItemDecoration.VERTICAL));
        mRv.setAdapter(mSoftwareManagerAdapter);
        mSoftwareManagerAdapter.setOnRvItemClickListener(new BaseRvAdapter.OnRvItemClickListener() {
            @Override
            public void onClick(final int position) {

                mExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        AppInfo currentAppInfo = mSoftwareManagerAdapter.getDataList().get(position);
                        boolean flag = currentAppInfo.isSelected();//先记住状态,「选中与否」

                        //这里需要遍历的原因是不知道用户会但点击哪一个项
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
                });
            }
        });
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void setListener() {
        if (mDisplayType == DISPLAY_USER_APP) {//暂时只监听用户卸载
            mUninstallReceiver = new UninstallReceiver();
            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
            intentFilter.addDataScheme("package");
            mBaseActivity.registerReceiver(mUninstallReceiver, intentFilter);
        }
    }

    private void fetchAppInfoAsync(final int fetchSuccessType) {
        mAllAppInfoList.clear();
        ProgressDialogUtil.showDefaultDialog(mBaseActivity);

        mExecutorService.execute((new Runnable() {
            @Override
            public void run() {
                mAllAppInfoList.addAll(AppInfoParser.getAppInfos(mBaseActivity));
                mSystemAppInfoList.clear();
                mUserAppInfoList.clear();

                for (AppInfo appInfo : mAllAppInfoList) {
                    if (appInfo.isUserApp()) {
                        mUserAppInfoList.add(appInfo);
                    } else {
                        mSystemAppInfoList.add(appInfo);
                    }
                }
                mHandler.sendEmptyMessage(fetchSuccessType);
            }
        }));
    }

    @Override
    public void onDestroyView() {
        //释放监听器
        if (mUninstallReceiver != null) {
            mBaseActivity.unregisterReceiver(mUninstallReceiver);
        }
        super.onDestroyView();
    }

    private class UninstallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String packageName = intent.getData().getSchemeSpecificPart();
            showToast("应用卸载成功");
            //刷新应用数据
            fetchAppInfoAsync(REMOVE_APP_SUCCESS);
        }
    }

}
