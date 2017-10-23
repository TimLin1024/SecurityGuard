package com.android.rdc.mobilesafe.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.util.Locale;

public class RoundProgress extends View {
    private static final String TAG = "RoundProgress";
    private Paint mPaint = new Paint();
    private int mWidth;
    private int mHeight;
    private float mCurrentProcess;
    private int mCenterFontSize;
    private float mTotal = 100;


    public RoundProgress(Context context) {
        super(context);
    }

    public RoundProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mCenterFontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, context.getResources().getDisplayMetrics());//使用带单位的字体大小
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);

        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                measureWidth = 200;
                break;
        }
        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                measureHeight = 200;
                break;
        }
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }


    public void updateProcess(float currentProcess) {
        mCurrentProcess = currentProcess;
        if (currentProcess > 100) {
            return;
        }
        invalidate();
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(35);
        mPaint.setAntiAlias(true);
        canvas.translate(mWidth / 2, mHeight / 2);
        float radius = (float) (Math.min(mWidth, mHeight) / 2);
        float left = radius / 2;
        final RectF rectF = new RectF(-left, -left, left, left);

        float percentage = mCurrentProcess / mTotal;
        float sweepAngle = 360 * percentage;

        mPaint.setColor(Color.GRAY);
        canvas.drawArc(rectF, 0, 360, false, mPaint);//绘制初始圆环
        //绘制已经加载的进度
        if (mCurrentProcess >= 100) {
            mPaint.setColor(Color.GREEN);
        } else {
            mPaint.setColor(Color.CYAN);
        }
        canvas.drawArc(rectF, -90, sweepAngle, false, mPaint);

        //绘制中间的文字
        mPaint.setTextSize(mCenterFontSize);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLACK);


        String str = String.format(Locale.CHINA, "%d%%", (int) mCurrentProcess);
        Rect rect = new Rect();
        mPaint.getTextBounds(str, 0, str.length(), rect);
        Log.d(TAG, "onDraw:rect.width() " + rect.width());
        Log.d(TAG, "onDraw:rect.height() " + rect.height());

        canvas.drawText(str, -rect.width() / 2, rect.height() / 2, mPaint);
    }
}
