package com.android.rdc.mobilesafe.base;


import android.os.Handler;
import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;

public class BaseSafeFragmentHandler<T extends Fragment> extends Handler {
    private final WeakReference<T> mFragmentReference;

    public BaseSafeFragmentHandler(T fragment) {
        mFragmentReference = new WeakReference<>(fragment);
    }

    protected T getFragment() {
        T fragment = mFragmentReference.get();
        //判断 activity 是否为空，以及是否正在被销毁、或者已经被销毁
        if (fragment == null || fragment.isDetached() || fragment.isRemoving()) {
            removeCallbacksAndMessages(null);//清空所有消息和回调
            return null;
        }
        return fragment;
    }
}
