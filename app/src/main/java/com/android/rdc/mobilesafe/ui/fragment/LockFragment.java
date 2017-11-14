package com.android.rdc.mobilesafe.ui.fragment;


import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.adapter.LockRvAdapter;
import com.android.rdc.mobilesafe.base.BaseFragment;
import com.android.rdc.mobilesafe.bean.AppInfo;
import com.android.rdc.mobilesafe.dao.AppLockDao;
import com.android.rdc.mobilesafe.util.AppInfoParser;

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
        mAllAppInfoList = AppInfoParser.getAppInfos(mBaseActivity.getApplicationContext());//获取本机安装的所有应用数据
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
//                mRvLock.setItemAnimator(new DefaultItemAnimator());
                mTvLock.setText(String.format(Locale.CHINA, "已加锁应用共有：%d 个", mLockAppInfoList.size()));
            });
        } else {
            mLockRvAdapter.notifyDataSetChanged();
        }
    }
}
