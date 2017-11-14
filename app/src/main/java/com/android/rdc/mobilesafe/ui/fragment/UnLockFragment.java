package com.android.rdc.mobilesafe.ui.fragment;


import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.adapter.LockRvAdapter;
import com.android.rdc.mobilesafe.base.BaseFragment;
import com.android.rdc.mobilesafe.base.BaseSafeFragmentHandler;
import com.android.rdc.mobilesafe.bean.AppInfo;
import com.android.rdc.mobilesafe.constant.Constant;
import com.android.rdc.mobilesafe.dao.AppLockDao;
import com.android.rdc.mobilesafe.util.AppInfoParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;


public class UnLockFragment extends BaseFragment {

    @BindView(R.id.tv_unlock)
    TextView mTvUnlock;
    @BindView(R.id.rv_unlock)
    RecyclerView mRvUnlock;

    private List<AppInfo> mUnlockList = new ArrayList<>();
    private List<AppInfo> mAllAppInfoList;
    private AppLockDao mAppLockDao;
    private LockRvAdapter mLockRvAdapter;
    private Uri mUri = Uri.parse(Constant.URI_STR);
    private Handler mHandler = new UnLockFragmentHandler(this);
    private ContentObserver mObserver;

    private static class UnLockFragmentHandler extends BaseSafeFragmentHandler<UnLockFragment> {

        UnLockFragmentHandler(UnLockFragment fragment) {
            super(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            UnLockFragment unLockFragment = getFragment();
            if (unLockFragment == null) {
                return;
            }
//            switch (msg.what) {
//                case 100:
//                    unLockFragment.mUnlockList.clear();
//                    unLockFragment.mUnlockList.addAll((Collection<? extends AppInfo>) msg.obj);
//
//                    if (unLockFragment.mLockRvAdapter == null) {
//                        unLockFragment.mLockRvAdapter = new LockRvAdapter();
//                        unLockFragment.mLockRvAdapter.setDataList(unLockFragment.mUnlockList);
//                        unLockFragment.mRvUnlock.setAdapter(unLockFragment.mLockRvAdapter);
//                        unLockFragment.initListener();
//                    } else {
//                        unLockFragment.mLockRvAdapter.notifyDataSetChanged();
//                    }
//                    unLockFragment.mTvUnlock.setText(String.format(Locale.CHINA, "未加锁应用共：%d", unLockFragment.mUnlockList.size()));
//                    break;
//            }
        }
    }

    public static UnLockFragment newInstance() {
        return new UnLockFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mAppLockDao = AppLockDao.getInstance(mBaseActivity.getApplicationContext());
        mAllAppInfoList = AppInfoParser.getAppInfos(mBaseActivity.getApplicationContext());//获取手机安装的所有应用的数据
        mObserver = new ContentObserver(mHandler) {
            @Override
            public void onChange(boolean selfChange) {
                fillData();
            }
        };


        return view;
    }

    @Override
    public void onResume() {
        fillData();
        super.onResume();
        /**
         * 数据库添加和删除都做了一个操作——通知内容观察者某个 uri 的数据发生了改变，
         * 此处使用该 uri 注册一个 内容观察者，如果数据内容改变会回调 onChange( ) 方法
         * */
        mBaseActivity.getContentResolver().registerContentObserver(mUri, true, mObserver);
    }

    private void initListener() {
        mLockRvAdapter.setOnRvItemClickListener(position -> {
            if (position == RecyclerView.NO_POSITION) {
                return;
            }
            mAppLockDao.insert(mUnlockList.get(position).getPackageName());
            mUnlockList.remove(position);
            mLockRvAdapter.notifyItemRemoved(position);
            mTvUnlock.setText(String.format(Locale.CHINA, "未加锁应用： %d", mUnlockList.size()));
        });
    }

    private void fillData() {
        final List<AppInfo> appInfoList = new ArrayList<>();
        for (AppInfo appInfo : mAllAppInfoList) {
            if (!mAppLockDao.find(appInfo.getPackageName())) {//查看是否在应用锁数据库中，如果不在，则添加到未加锁列表中
                appInfoList.add(appInfo);
            }
        }

        mUnlockList.clear();
        mUnlockList.addAll(appInfoList);

        if (mLockRvAdapter == null) {
            mLockRvAdapter = new LockRvAdapter();
            mLockRvAdapter.setDataList(mUnlockList);
            mRvUnlock.setAdapter(mLockRvAdapter);
            mRvUnlock.setLayoutManager(new LinearLayoutManager(mBaseActivity));
            mRvUnlock.addItemDecoration(new DividerItemDecoration(mBaseActivity, DividerItemDecoration.VERTICAL));
            initListener();
        } else {
            mLockRvAdapter.notifyDataSetChanged();
        }
        mTvUnlock.setText(String.format(Locale.CHINA, "未加锁应用共：%d 个", mUnlockList.size()));
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_unlock;
    }

    @Override
    protected void initData(Bundle bundle) {
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void setListener() {
    }

    @Override
    public void onDestroyView() {
        mBaseActivity.getContentResolver().unregisterContentObserver(mObserver);
        super.onDestroyView();
    }
}
