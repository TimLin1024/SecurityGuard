package com.android.rdc.mobilesafe.ch09;


import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseFragment;
import com.android.rdc.mobilesafe.base.BaseRvAdapter;
import com.android.rdc.mobilesafe.adapter.LockRvAdapter;
import com.android.rdc.mobilesafe.ch09.entity.AppInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class LockFragment extends BaseFragment {
    @BindView(R.id.tv_lock)
    TextView mTvLock;
    @BindView(R.id.rv_lock)
    RecyclerView mRvLock;

    private List<AppInfo> mAppInfoList;
    private LockRvAdapter mLockRvAdapter;


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
        mAppInfoList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            AppInfo info = new AppInfo();
            info.mPackageName = "test" + i;
            info.mAppName = "t" + i;
            info.mIcon = getResources().getDrawable(R.drawable.lock);
            mAppInfoList.add(info);
        }
    }

    @Override
    protected void initView() {
        mLockRvAdapter = new LockRvAdapter();
        mLockRvAdapter.setDataList(mAppInfoList);
        mRvLock.setAdapter(mLockRvAdapter);
        mRvLock.setLayoutManager(new LinearLayoutManager(mBaseActivity));

    }

    @Override
    protected void setListener() {
        mLockRvAdapter.setOnRvItemClickListener(new BaseRvAdapter.OnRvItemClickListener() {
            @Override
            public void onClick(int position) {

                mAppInfoList.remove(position);
//                mLockRvAdapter.notifyDataSetChanged();
                mLockRvAdapter.notifyItemRangeRemoved(position, 1);
                mRvLock.setItemAnimator(new DefaultItemAnimator());
                mTvLock.setText("已加锁应用共有： " + mAppInfoList.size());

            }
        });
    }
}
