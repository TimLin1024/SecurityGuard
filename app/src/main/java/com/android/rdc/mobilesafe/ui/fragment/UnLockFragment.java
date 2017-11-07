package com.android.rdc.mobilesafe.ui.fragment;


import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.adapter.LockRvAdapter;
import com.android.rdc.mobilesafe.base.BaseFragment;
import com.android.rdc.mobilesafe.base.BaseRvAdapter;
import com.android.rdc.mobilesafe.constant.Constant;
import com.android.rdc.mobilesafe.dao.AppLockDao;
import com.android.rdc.mobilesafe.bean.AppInfo;
import com.android.rdc.mobilesafe.util.AppInfoParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;


public class UnLockFragment extends BaseFragment {

    @BindView(R.id.tv_unlock)
    TextView mTvUnlock;
    @BindView(R.id.rv_unlock)
    RecyclerView mRvUnlock;

    private List<AppInfo> mUnlockList = new ArrayList<>();
    private List<AppInfo> mAppInfoList;
    private AppLockDao mAppLockDao;
    private LockRvAdapter mLockRvAdapter;
    private Uri mUri = Uri.parse(Constant.URI_STR);

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    mUnlockList.clear();
                    mUnlockList.addAll((Collection<? extends AppInfo>) msg.obj);

                    if (mLockRvAdapter == null) {
                        mLockRvAdapter = new LockRvAdapter();
                        mLockRvAdapter.setDataList(mUnlockList);
                        mRvUnlock.setAdapter(mLockRvAdapter);
                        initListener();
                    } else {
                        mLockRvAdapter.notifyDataSetChanged();
                    }
                    String str = "未加锁应用共：" + mUnlockList.size();
                    mTvUnlock.setText(str);
                    break;
            }
        }
    };

    public static UnLockFragment newInstance() {
        UnLockFragment fragment = new UnLockFragment();
        return fragment;
    }


    @Override
    public void onResume() {
        mAppLockDao = AppLockDao.getInstance(mBaseActivity);
        mAppInfoList = AppInfoParser.getAppInfos(mBaseActivity);
        fillData();
        super.onResume();
        /**
         * 数据库添加和删除都做了一个操作——通知内容观察者某个 uri 的数据发生了改变，
         * 此处使用该 uri 注册一个 内容观察者，如果数据内容改变会回调 onChange( ) 方法
         * */
        mBaseActivity.getContentResolver().registerContentObserver(mUri, true, new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                fillData();
            }
        });

    }

    private void initListener() {
        mLockRvAdapter.setOnRvItemClickListener(new BaseRvAdapter.OnRvItemClickListener() {
            @Override
            public void onClick(int position) {
                if (position == RecyclerView.NO_POSITION) {
                    return;
                }
                mAppLockDao.insert(mUnlockList.get(position).getPackageName());
                mUnlockList.remove(position);
                mRvUnlock.setItemAnimator(new DefaultItemAnimator());
                mLockRvAdapter.notifyItemRemoved(position);
                mTvUnlock.setText("未加锁应用： " + mUnlockList.size());
            }
        });
    }

    private void fillData() {
        final List<AppInfo> appInfoList = new ArrayList<>();
        for (AppInfo appInfo : mAppInfoList) {
            if (!mAppLockDao.find(appInfo.getPackageName())) {
                appInfoList.add(appInfo);
            }
        }

        mUnlockList.clear();
        mUnlockList.addAll(appInfoList);

        if (mLockRvAdapter == null) {
            mLockRvAdapter = new LockRvAdapter();
            mLockRvAdapter.setDataList(mUnlockList);
            mRvUnlock.setAdapter(mLockRvAdapter);
            initListener();
        } else {
            mLockRvAdapter.notifyDataSetChanged();
        }
//                    mRvUnlock.notify();
        String str = "未加锁应用共：" + mUnlockList.size();
        mTvUnlock.setText(str);

    }
//    private void fillData() {
//        final List<AppInfo> appInfoList = new ArrayList<>();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (AppInfo appInfo : mAppInfoList) {
//                    if (!mAppLockDao.find(appInfo.mPackageName)) {
//                        appInfoList.add(appInfo);
//                    }
//                }
//                // TODO: 2017/7/4 0004 没有 new 使用了 handler
//                Message message = mHandler.obtainMessage();
//                message.obj = appInfoList;
//                message.what = 100;
//                mHandler.sendMessage(message);
//            }
//        }).start();
//    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_unlock;
    }

    @Override
    protected void initData(Bundle bundle) {

    }

    @Override
    protected void initView() {
        mRvUnlock.setLayoutManager(new LinearLayoutManager(mBaseActivity));
    }

    @Override
    protected void setListener() {

    }
}
