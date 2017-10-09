package com.android.rdc.mobilesafe.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.android.rdc.mobilesafe.R;

/**
 * 侧滑退出/进入 Activity
 */
public abstract class BaseScrollTbActivity extends BaseToolBarActivity {
    private GestureDetectorCompat mGestureDetector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化手势监听器
        mGestureDetector = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (Math.abs(velocityX) < 200) {
                    showToast("速度太慢，无效动作");
                    return true;
                }

                if (e1.getRawX() - e2.getRawX() > 200) {//从右向左滑动,显示下一个
                    overridePendingTransition(R.anim.next_in, R.anim.next_out);
                    showNext();
                    return true;
                }

                if (e2.getRawX() - e1.getRawX() > 200) {//从左向右滑动，显示上一个
                    overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
                    showPre();
                    return true;
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public abstract void showPre();

    public abstract void showNext();

    public void startActivityAndFinishSelf(Class clazz) {
        startActivity(clazz);
        finish();
    }
}
