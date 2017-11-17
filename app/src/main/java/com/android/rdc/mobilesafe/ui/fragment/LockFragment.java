package com.android.rdc.mobilesafe.ui.fragment;


import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.adapter.LockRvAdapter;
import com.android.rdc.mobilesafe.base.BaseFragment;
import com.android.rdc.mobilesafe.bean.AppInfo;
import com.android.rdc.mobilesafe.constant.Constant;
import com.android.rdc.mobilesafe.dao.AppLockDao;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;


public class LockFragment extends BaseFragment {
    private static final String TAG = "LockFragment";
    @BindView(R.id.tv_lock)
    TextView mTvLock;
    @BindView(R.id.rv_lock)
    RecyclerView mRvLock;

    private List<AppInfo> mAllAppInfoList = new ArrayList<>();
    private List<AppInfo> mLockAppInfoList = new ArrayList<>();
    private LockRvAdapter mLockRvAdapter;
    private AppLockDao mAppLockDao;
    private ContentObserver mContentObserver;

    public static LockFragment newInstance() {
        LockFragment fragment = new LockFragment();
        return fragment;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_lock;
    }

    @Override
    protected void initData(Bundle bundle) {
        mAppLockDao = AppLockDao.getInstance(mBaseActivity.getApplicationContext());
//        mExecutorService = Executors.newFixedThreadPool(1);
//        mExecutorService.execute(() -> {
//            ProgressDialogUtil.showDefaultDialog(getActivity());
//            mAllAppInfoList = AppInfoParser.getAppInfos(mBaseActivity.getApplicationContext());//获取本机安装的所有应用数据
//            mBaseActivity.runOnUiThread(() -> {
//                mIsFirstIn = false;
//                ProgressDialogUtil.dismiss();
//                fillData();
//            });
//        });
        mContentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                fillData();
            }
        };
        Uri uri = Uri.parse(Constant.URI_APP_LOCK_DB);
        mBaseActivity.getContentResolver().registerContentObserver(uri, true, mContentObserver);//注册为监听者
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void setListener() {
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        fillData();
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        mBaseActivity.getContentResolver().unregisterContentObserver(mContentObserver);
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    private void fillData() {
        mLockAppInfoList.clear();
        for (AppInfo appInfo : mAllAppInfoList) {
            if (mAppLockDao.find(appInfo.getPackageName())) {//如果在应用锁表中，则添加到列表中显示
                mLockAppInfoList.add(appInfo);
            }
        }
        if (mLockRvAdapter == null) {
            mLockRvAdapter = new LockRvAdapter();
            mLockRvAdapter.setDataList(mLockAppInfoList);
            mRvLock.setAdapter(mLockRvAdapter);
            mRvLock.setLayoutManager(new LinearLayoutManager(mBaseActivity));
            mRvLock.addItemDecoration(new DividerItemDecoration(mBaseActivity, DividerItemDecoration.VERTICAL));
            mLockRvAdapter.setOnRvItemClickListener(position -> {
                mAppLockDao.delete(mLockAppInfoList.get(position).getPackageName());//从数据库表中移除
                mLockAppInfoList.remove(position);
                mLockRvAdapter.notifyItemRangeRemoved(position, 1);
                mRvLock.setItemAnimator(new DefaultItemAnimator());
                mTvLock.setText(String.format(Locale.CHINA, "已加锁应用共有：%d 个", mLockAppInfoList.size()));
            });
        } else {
            mLockRvAdapter.notifyDataSetChanged();
        }
        mTvLock.setText(String.format(Locale.CHINA, "已加锁应用共有：%d 个", mLockAppInfoList.size()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN,priority = 100)
    public void onAppDataFetch(List<AppInfo> appInfoList) {
        mAllAppInfoList = appInfoList;
        fillData();
    }
}
