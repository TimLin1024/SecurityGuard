package com.android.rdc.mobilesafe.base;

import java.lang.ref.WeakReference;

public abstract class BaseSafeRunnable<T> implements Runnable {
    protected WeakReference<T> mReference;

    public BaseSafeRunnable(T reference) {
        mReference = new WeakReference<>(reference);
    }

}
