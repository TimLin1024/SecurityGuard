package com.android.rdc.mobilesafe.ui;

import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.adapter.CleanCacheAdapter;
import com.android.rdc.mobilesafe.base.BaseToolBarActivity;
import com.android.rdc.mobilesafe.entity.CacheInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class CacheListActivity extends BaseToolBarActivity {

    @BindView(R.id.tv_total_cache)
    TextView mTvTotalCache;
    @BindView(R.id.tv_unit_type)
    TextView mTvUnitType;
    @BindView(R.id.tv_scanning_file)
    TextView mTvScanningFile;
    @BindView(R.id.iv_animation)
    ImageView mIvAnimation;
    @BindView(R.id.rv)
    RecyclerView mRv;
    @BindView(R.id.btn_clean_cache)
    Button mBtnCleanCache;

    private static final int FINISH = 101;
    private static final int SCANNING = 102;


    private PackageManager mPackageManager;
    private CleanCacheAdapter mAdapter;
    private AnimationDrawable mAnimationDrawable;
    private List<CacheInfo> mCacheInfoList = new ArrayList<>();
    private Thread mThread;

    private long mCacheMem;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case FINISH:
                    mTvScanningFile.setText("扫描完成");
                    mAnimationDrawable.stop();

                    mBtnCleanCache.setEnabled(mCacheMem > 0);
                    break;
                case SCANNING:
                    PackageInfo packageInfo = (PackageInfo) msg.obj;
                    mTvScanningFile.setText("正在扫描" + packageInfo.packageName);
                    String[] str = Formatter.formatFileSize(CacheListActivity.this, mCacheMem).split(" ");
                    mTvTotalCache.setText(str[0]);
                    mTvUnitType.setText(str[1]);
//                    mTvUnitType
//                    mCacheInfoList.addAll()
//                    mCacheInfoList.add(packageInfo);
                    mAdapter.notifyDataSetChanged();
                    mRv.scrollToPosition(mCacheInfoList.size() - 1);
                    break;
            }
        }
    };


    /**
     * 缓存的获取、清理
     */
    @Override
    protected int setResId() {
        return R.layout.activity_cache_list;
    }

    @Override
    protected void initData() {
        mAdapter = new CleanCacheAdapter();
        mAdapter.setDataList(mCacheInfoList);
        mRv.setAdapter(mAdapter);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mPackageManager = getPackageManager();

        mAnimationDrawable = (AnimationDrawable) mIvAnimation.getBackground();
        mAnimationDrawable.setOneShot(false);
        mAnimationDrawable.start();

        mBtnCleanCache.setEnabled(false);
        fetchCacheInfoList();
    }

    private void fetchCacheInfoList() {
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mCacheInfoList.clear();
                //遍历手机中所有的 app
                List<PackageInfo> packageInfoList = mPackageManager.getInstalledPackages(0);
                for (PackageInfo packageInfo : packageInfoList) {
                    getCacheSize(packageInfo);
                    // TODO: 2017/9/29 0029 为什么要睡眠
                    try {
                        Thread.sleep(60);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message message = mHandler.obtainMessage();
                    message.what = SCANNING;
                    message.obj = packageInfo;
                    mHandler.sendMessage(message);
                }
                mHandler.sendEmptyMessage(FINISH);
            }
        });
        mThread.start();
    }

    private void getCacheSize(PackageInfo packageInfo) {
        Method method;
        try {
            method = PackageManager.class.getDeclaredMethod("getPackageSizeInfo", String.class,//反射获取方法
                    IPackageStatsObserver.class);
            method.invoke(mPackageManager, packageInfo.packageName, new PackageDataObserver(packageInfo));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        mAnimationDrawable.stop();
        if (mThread != null) {
            mThread.interrupt();
        }
        super.onDestroy();
    }

    @OnClick(R.id.btn_clean_cache)
    public void onViewClicked() {
        startActivity(CleanCacheActivity.newIntent(this, mCacheMem));
        finish();
    }

    private class PackageDataObserver extends IPackageStatsObserver.Stub {

        private PackageInfo mPackageInfo;

        public PackageDataObserver(PackageInfo packageInfo) {
            mPackageInfo = packageInfo;
        }

        // TODO: 2017/9/29 0029 怎么知道获取的缓存大小是哪一个 app 的呢？
        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            long cacheSize = pStats.cacheSize;//获取缓存大小
            if (cacheSize >= 0) {
                CacheInfo cacheInfo = new CacheInfo();
                cacheInfo.setCacheSize(cacheSize);
                cacheInfo.setAppName(mPackageInfo.applicationInfo.loadLabel(mPackageManager).toString());
                cacheInfo.setIcon(mPackageInfo.applicationInfo.loadIcon(mPackageManager));
                cacheInfo.setPackageName(mPackageInfo.packageName);
                mCacheInfoList.add(cacheInfo);
                mCacheMem += cacheSize;
            }
        }
    }
}
