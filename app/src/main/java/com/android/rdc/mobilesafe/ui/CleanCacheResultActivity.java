package com.android.rdc.mobilesafe.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.constraint.ConstraintLayout;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseActivity;
import com.android.rdc.mobilesafe.base.BaseSafeActivityHandler;

import java.lang.reflect.Method;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

public class CleanCacheResultActivity extends BaseActivity {
    private static final String KEY_CACHE_MEMORY = "CACHE_MEMORY";
    private static final int CLEANING = 1;

    @BindView(R.id.iv_cleaning_icon)
    ImageView mIvCleaningIcon;
    @BindView(R.id.tv_total_cache_clean)
    TextView mTvTotalCacheClean;
    @BindView(R.id.tv_unit_type)
    TextView mTvUnitType;
    @BindView(R.id.cl_cleaning)
    ConstraintLayout mClCleaning;
    @BindView(R.id.btn_finish_clean_cache)
    Button mBtnFinishCleanCache;
    @BindView(R.id.cl_finish)
    ConstraintLayout mClFinish;

    private long mCacheMem;
    private PackageManager mPackageManager;
    private Handler mHandler = new CleanCacheHandler(this);

    private static class CleanCacheHandler extends BaseSafeActivityHandler<CleanCacheResultActivity> {

        CleanCacheHandler(CleanCacheResultActivity activityReference) {
            super(activityReference);
        }

        @Override
        public void handleMessage(Message msg) {
            CleanCacheResultActivity cleanCacheResultActivity = getActivity();
            if (cleanCacheResultActivity == null) {
                return;
            }
            switch (msg.what) {
                case CLEANING:
                    long mem = (long) msg.obj;
                    String[] str = Formatter.formatFileSize(cleanCacheResultActivity, mem).split(" ");
                    cleanCacheResultActivity.mTvTotalCacheClean.setText(str[0]);
                    cleanCacheResultActivity.mTvUnitType.setText(str.length > 1 ? str[1] : "");
            }
        }
    }

    public static Intent newIntent(Context context, long cacheMem) {
        Intent intent = new Intent(context, CleanCacheResultActivity.class);
        intent.putExtra(KEY_CACHE_MEMORY, cacheMem);
        return intent;
    }

    @Override
    protected int setResId() {
        return R.layout.activity_clean_cache_result;
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            mCacheMem = getIntent().getLongExtra(KEY_CACHE_MEMORY, 0);
        }
        mPackageManager = getApplicationContext().getPackageManager();
        cleanAll();

        new Thread(() -> {
            long mem = 0;
            //这里假装在清理，实际上是一次清空
            while (mem < mCacheMem) {
                Random random = new Random();
                mem += 1024 * random.nextInt();
                if (mem > mCacheMem) {
                    mem = mCacheMem;
                }
                Message message = mHandler.obtainMessage();
                message.obj = mem;
                message.what = CLEANING;
                mHandler.sendMessage(message);
            }
        }).start();
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

    @OnClick(R.id.btn_finish_clean_cache)
    public void onViewClicked() {
        finish();
    }

    class CleanCacheObserver extends IPackageDataObserver.Stub {

        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
            runOnUiThread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                showToast("缓存清理完毕,您的手机洁净如新");
                mClCleaning.setVisibility(View.GONE);
                mClFinish.setVisibility(View.VISIBLE);
            });
        }
    }

    private void cleanAll() {
        Method[] methods = PackageManager.class.getMethods();
        for (Method method : methods) {
            if ("freeStorageAndNotify".equals(method.getName())) {
                try {
                    method.invoke(mPackageManager, Long.MAX_VALUE, new CleanCacheObserver());//清空缓存
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
}
