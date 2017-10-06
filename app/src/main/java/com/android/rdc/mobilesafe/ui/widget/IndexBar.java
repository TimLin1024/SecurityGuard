package com.android.rdc.mobilesafe.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;

import java.util.Arrays;
import java.util.List;

public class IndexBar extends View {

    //默认的数据源
    public static String[] INDEX_STRING = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};

    private Paint mPaint;
    private List<String> mIndexData;
    private int mGapHeight;
    private int mWidth;
    private int mHeight;


    private OnIndexPressedListener mOnIndexPressedListener;
    private TextView mHintTextView;
    private LinearLayoutManager mLayoutManager;
    private int mPressedBg;
    private List<? extends BaseTagBean> mSourceData;

    public IndexBar(Context context) {
        this(context, null);
    }

    public IndexBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndexBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        int textSize = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
        mPressedBg = Color.BLACK;
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.IndexBar, defStyleAttr, 0);
        int n = typedArray.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.IndexBar_indexBarTextSize) {
                textSize = typedArray.getDimensionPixelSize(attr, textSize);
            } else if (attr == R.styleable.IndexBar_indexBarPressBackground) {
                mPressedBg = typedArray.getColor(attr, mPressedBg);
            }
        }
        typedArray.recycle();

        initDefaultIndexData();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(textSize);
        mPaint.setColor(Color.BLACK);

        setOnIndexPressedListener(new OnIndexPressedListener() {
            @Override
            public void onIndexPressed(int index, String text) {
                if (mHintTextView != null) {
                    mHintTextView.setVisibility(VISIBLE);
                    mHintTextView.setText(text);
                }
                //滑动到选中位置的 index
                if (mLayoutManager != null) {
                    int position = getPosByTag(text);
                    if (position != -1) {
                        mLayoutManager.scrollToPositionWithOffset(position, 0);
                    }
                }
            }

            @Override
            public void onMotionEventEnd() {
                //手指抬起时背景恢复透明
                setBackgroundResource(android.R.color.transparent);
                if (mHintTextView != null) {
                    mHintTextView.setVisibility(GONE);
                }
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        if (null == mIndexData || mIndexData.isEmpty()) {
            return;
        }
        computeGapHeight();
    }

    private void computeGapHeight() {
        mGapHeight = (mHeight - getPaddingTop() - getPaddingBottom()) / mIndexData.size();
    }

    private void initDefaultIndexData() {
        mIndexData = Arrays.asList(INDEX_STRING);
    }

    private int getPosByTag(String text) {
        if (mSourceData == null || mSourceData.isEmpty()
                || text == null) {
            return -1;
        }
        int len = mSourceData.size();

        for (int i = 0; i < len; i++) {
            String tag = mSourceData.get(i).getTag();
            if (tag != null && text.equalsIgnoreCase(tag)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);

        int measureWidth = 0;
        int measureHeight = 0;

        Rect indexBound = new Rect();
        for (int i = 0; i < mIndexData.size(); i++) {
            String index = mIndexData.get(i);
            mPaint.getTextBounds(index, 0, index.length(), indexBound);
            measureWidth = Math.max(indexBound.width(), measureWidth);
            measureHeight = Math.max(indexBound.height(), measureHeight);
        }
        measureHeight *= mIndexData.size();
        switch (wMode) {
            case MeasureSpec.EXACTLY:
                measureWidth = wSize;
                break;
            case MeasureSpec.AT_MOST:
                measureWidth = Math.min(measureWidth, wSize);//wSize 是父控件能给子 View 的最大宽度
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }
        switch (hMode) {
            case MeasureSpec.EXACTLY:
                measureHeight = hSize;
                break;
            case MeasureSpec.AT_MOST:
                measureHeight = Math.min(measureHeight, hSize);//wSize 是父控件能给子 View 的最大宽度
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        int t = getPaddingTop();
        String index;
        for (int i = 0; i < mIndexData.size(); i++) {
            index = mIndexData.get(i);
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            int baseline = (int) ((mGapHeight - fontMetrics.bottom - fontMetrics.top) / 2);
            canvas.drawText(index, mWidth / 2 - mPaint.measureText(index) / 2, t + mGapHeight * i + baseline, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setBackgroundColor(mPressedBg);
                //此处没有 break
            case MotionEvent.ACTION_MOVE:
                //获取 index，滑动到指定索引位置
                float y = event.getY();//点击位置相对于 View 的 y 左边
                int pressI = (int) ((y - getPaddingTop()) / mGapHeight);//滑动到位置的 y 坐标
                //防止越界
                if (pressI < 0) {
                    pressI = 0;
                } else if (pressI > mIndexData.size() - 1) {
                    pressI = mIndexData.size() - 1;
                }
                //回调监听器方法
                if (mOnIndexPressedListener != null) {
                    mOnIndexPressedListener.onIndexPressed(pressI, mIndexData.get(pressI));
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mOnIndexPressedListener != null) {
                    mOnIndexPressedListener.onMotionEventEnd();
                }
                break;
            default:
                break;
        }
        return true;
    }

    public void setHintTextView(TextView hintTextView) {
        mHintTextView = hintTextView;
    }

    public void setLayoutManager(LinearLayoutManager layoutManager) {
        mLayoutManager = layoutManager;
    }

    public void setPressedBg(int pressedBg) {
        mPressedBg = pressedBg;
    }

    public void setSourceData(List<? extends BaseTagBean> sourceData) {
        mSourceData = sourceData;
    }

    public OnIndexPressedListener getOnIndexPressedListener() {
        return mOnIndexPressedListener;
    }

    public void setOnIndexPressedListener(OnIndexPressedListener onIndexPressedListener) {
        mOnIndexPressedListener = onIndexPressedListener;
    }

    /**
     * 当前被按下的index的监听器
     */
    public interface OnIndexPressedListener {
        void onIndexPressed(int index, String text);//当某个Index被按下

        void onMotionEventEnd();//当触摸事件结束（UP CANCEL）
    }
}