package com.android.rdc.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.rdc.mobilesafe.ui.EnterPwdActivity;
import com.android.rdc.mobilesafe.dao.AppLockDao;
import com.android.rdc.mobilesafe.constant.Constant;

import java.util.List;

import static com.android.rdc.mobilesafe.constant.Constant.FILTER_ACTION;
import static com.android.rdc.mobilesafe.constant.Constant.KEY_EXTRA_PACKAGE_NAME;

public class AppLockService extends Service {
    private static final String TAG = "AppLockService";
    private boolean flag;//是否启动
    private ActivityManager mActivityManager;
    private List<ActivityManager.RunningTaskInfo> mRunningTaskInfoList;
    private ActivityManager.RunningTaskInfo mRunningTaskInfo;
    private String mPackageName;
    private List<String> mPackageList;
    private String mTmpStopProtectPackageName;//最近使用的应用
    private AppLockDao mAppLockDao;
    private AppLockObserver mAppLockObserver;
    private Uri mUri = Uri.parse(Constant.URI_APP_LOCK_DB);
    private AppLockReceiver mAppLockReceiver;
    private Intent mIntent;

//    public static final String FILTER_ACTION = "com.android.rdc.mobilesafe.applock";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mAppLockDao = AppLockDao.getInstance(this);
        mAppLockObserver = new AppLockObserver(new Handler());
        mPackageList = mAppLockDao.findAll();
        mAppLockReceiver = new AppLockReceiver();
        IntentFilter intentFilter = new IntentFilter(FILTER_ACTION);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mAppLockReceiver, intentFilter);

        mIntent = new Intent(this, EnterPwdActivity.class);

        getContentResolver().registerContentObserver(mUri, true, mAppLockObserver);
        mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        startAppLockService();
        super.onCreate();
    }

    private void startAppLockService() {
        new Thread(() -> {
            flag = true;
            Log.e(TAG, "run: 服务即将开启");
//                Toast.makeText(AppLockService.this, "服务即将开启", Toast.LENGTH_SHORT).show();
            while (flag) {
                // 5.0 以上用不了
                mRunningTaskInfoList = mActivityManager.getRunningTasks(1);
                mRunningTaskInfo = mRunningTaskInfoList.get(0);
                mPackageName = mRunningTaskInfo.topActivity.getPackageName();
                Log.e(TAG, "run: mTmpStopProtectPackageName 00" + mTmpStopProtectPackageName);
                Log.e(TAG, "run: 开启中");
                if (mPackageList.contains(mPackageName)) {
                    //判断是否需要保护
                    if (!mPackageName.equals(mTmpStopProtectPackageName)) {
                        Log.e(TAG, "run: mTmpStopProtectPackageName 01" + mTmpStopProtectPackageName);
                        //需要保护
                        mIntent.putExtra(KEY_EXTRA_PACKAGE_NAME, mPackageName);
                        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(mIntent);
                    }
                }

                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public void onDestroy() {
        flag = false;
        unregisterReceiver(mAppLockReceiver);
        mAppLockReceiver = null;
        getContentResolver().unregisterContentObserver(mAppLockObserver);
        mAppLockObserver = null;
        super.onDestroy();
    }

    class AppLockReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case FILTER_ACTION:
                    mTmpStopProtectPackageName = intent.getStringExtra(Constant.KEY_EXTRA_PACKAGE_NAME);
                    Log.e(TAG, "onReceive: mTmpStopProtectPackageName =  " + mTmpStopProtectPackageName);
                    break;
                case Intent.ACTION_SCREEN_ON:
                    if (!flag) {
                        startAppLockService();
                    }
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    mTmpStopProtectPackageName = null;
                    flag = false;
                    break;
            }


        }
    }

    class AppLockObserver extends ContentObserver {
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public AppLockObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            mPackageList = mAppLockDao.findAll();
            super.onChange(selfChange);
        }
    }

}
