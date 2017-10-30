package com.android.rdc.mobilesafe.base;


import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public class BaseSafeActivityHandler<T extends AppCompatActivity> extends Handler {
    private final WeakReference<T> mActivityReference;

    public BaseSafeActivityHandler(T activityReference) {
        mActivityReference = new WeakReference<>(activityReference);
    }

    protected T getActivity() {
        T activity = mActivityReference.get();
        //判断 activity 是否为空，以及是否正在被销毁、或者已经被销毁
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            removeCallbacksAndMessages(null);//清空所有消息和回调
            return null;
        }
        return activity;
    }
}
