package com.android.rdc.mobilesafe.ui.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseSafeRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class RadarScanView extends View {
    private static final int DEFAULT_WIDTH = 80;
    private static final int DEFAULT_HEIGHT = 80;

    private int mDefaultWidth;
    private int mDefaultHeight;
    private int mStartScanDegree = 270;
    private int mStartClearDegree = 0;
    private int mCenterX;
    private int mCenterY;
    private int mRadarRadius;

    //OutSideCircleColor
    private int mCircleColor = Color.parseColor("#7197ED");
    private int mInnerCircleColor = Color.parseColor("#678EE6");
    private int mLayerColor = Color.parseColor("#30FAFAFA");

    private int mInnerTextColor = Color.parseColor("#FFFFFF");
    private float mInnerTextSize;

    private int mShaderColor1 = Color.parseColor("#00FAFAFA");
    private int mShaderColor2 = Color.parseColor("#59FAFAFA");
    private int mRadarLineColor = Color.WHITE;

    private float mBorderWidth = dip2px(getContext(), 10);

    private Paint mPaintCircle;
    private Paint mPaintInnerCircle;
    private Paint mPaintStroke;

    private Paint mPaintFillOutSize;
    private Paint mPaintStrokeOutSize;

    private Paint mPaintRadar;
    private Paint mPaintRadarLine;

    private Paint mPaintText;

    private Matrix mScanMatrix;

    private Paint mPaintClear;

    private boolean mIsPutWhiteLayer = false;
    private Canvas mLayerCanvas;

    private Bitmap mLayerBitmap;

//    private Handler mHandler = new Handler();

    private RectF mClearRect;

    private boolean mIsClearing = false;
    private boolean isScanning = false;

    private float mTextY;

    private boolean mIsShowText = true;

    private double mCollectionNum;
    private double mPieceOfNum;
    private String mUnit = "M";

    //默认清除时间3.6s
    private int mClearTime = 3600;

    private String mCenterText = "";//默认初始化，防止空指针

    private static class ScanRunnable extends BaseSafeRunnable<RadarScanView> {

        public ScanRunnable(RadarScanView reference) {
            super(reference);
        }

        @Override
        public void run() {
            RadarScanView radarScanView = mReference.get();
            if (radarScanView == null) {
                return;
            }
            radarScanView.mScanMatrix.reset();
            radarScanView.mStartScanDegree += 2;
            radarScanView.mScanMatrix.postRotate(radarScanView.mStartScanDegree, radarScanView.mCenterX, radarScanView.mCenterY);

            radarScanView.postInvalidate();
            if (radarScanView.isScanning) {
                radarScanView.postDelayed(radarScanView.mScanRunnable, 10);
            }
        }
    }


    private Runnable mScanRunnable = new ScanRunnable(this);

    // TODO: 2017/10/30 0030 在这里会引发内存泄漏
    private Runnable mClearRun = new Runnable() {
        @Override
        public void run() {
            if (mIsClearing && mStartClearDegree > -360) {
                mStartClearDegree -= 1;
                if (mCollectionNum > 0) {
                    mCollectionNum = mCollectionNum - mPieceOfNum;
                } else {
                    return;
                }
                postInvalidate();
                postDelayed(mClearRun, mClearTime / 360);
            } else {
                mIsClearing = false;
            }
        }
    };

    public RadarScanView(Context context) {
        super(context);
        init(null, context);
    }

    public RadarScanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, context);
    }

    public RadarScanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, context);
    }

    public RadarScanView(Context context, AttributeSet attrs,
                         int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        Log.i("Radar", w + "");

        mCenterX = w / 2;
        mCenterY = h / 2;
        //drawText高度会有偏差，这样的设置会让文字居中显示
        mTextY = mCenterY - ((mPaintText.descent() + mPaintText.ascent()) / 2);

        mRadarRadius = (int) (Math.min(mCenterX, mCenterY) - 2 * mBorderWidth);

        mLayerBitmap = Bitmap.createBitmap(2 * mRadarRadius, 2 * mRadarRadius, Bitmap.Config.ARGB_8888);
        mLayerCanvas = new Canvas(mLayerBitmap);
        mLayerCanvas.drawColor(mLayerColor);

        mClearRect = new RectF();
        mClearRect.bottom = 2 * mRadarRadius;
        mClearRect.top = 0;
        mClearRect.left = 0;
        mClearRect.right = 2 * mRadarRadius;
    }

    private void init(AttributeSet attrs, Context context) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs,
                    R.styleable.RadarScanView);
            mCircleColor = ta.getColor(R.styleable.RadarScanView_circleColor, mCircleColor);
            mInnerCircleColor = ta.getColor(R.styleable.RadarScanView_innerCircleColor, mInnerCircleColor);
            mLayerColor = ta.getColor(R.styleable.RadarScanView_layerColor, mLayerColor);

            mInnerTextColor = ta.getColor(R.styleable.RadarScanView_innerTextColor, mInnerTextColor);
            mInnerTextSize = ta.getDimension(R.styleable.RadarScanView_innerTextSize, mInnerTextSize);

            mShaderColor1 = ta.getColor(R.styleable.RadarScanView_radarShaderColor1, mShaderColor1);
            mShaderColor2 = ta.getColor(R.styleable.RadarScanView_radarShaderColor2, mShaderColor2);
            mRadarLineColor = ta.getColor(R.styleable.RadarScanView_radarLineColor, mRadarLineColor);

            mBorderWidth = ta.getDimension(R.styleable.RadarScanView_radarBorderWidth, mBorderWidth);

            ta.recycle();
        }

        mDefaultWidth = dip2px(context, DEFAULT_WIDTH);
        mDefaultHeight = dip2px(context, DEFAULT_HEIGHT);

        initPaint();
//        colors = new int[]{Color.parseColor("#00FAFAFA"),
//                Color.parseColor("#59FAFAFA")};
        //positions = new float[]{0, 1.0f};

        mScanMatrix = new Matrix();
    }

    private void initPaint() {
        mPaintCircle = new Paint();
        mPaintCircle.setColor(mCircleColor);
        mPaintCircle.setAntiAlias(true);
        mPaintCircle.setStyle(Paint.Style.FILL);//设置实心

        mPaintInnerCircle = new Paint();
        mPaintInnerCircle.setColor(mInnerCircleColor);
        mPaintCircle.setAntiAlias(true);
        mPaintCircle.setStyle(Paint.Style.FILL);//设置实心

        mPaintRadar = new Paint();
        mPaintRadar.setAntiAlias(true);

        mPaintRadarLine = new Paint();
        mPaintRadarLine.setColor(mRadarLineColor);
        mPaintRadarLine.setStrokeWidth(3);
        mPaintRadarLine.setAntiAlias(true);

        //whiteStokeCicle
        mPaintStroke = new Paint();
        mPaintStroke.setColor(Color.parseColor("#AEC4F4"));
        mPaintStroke.setAntiAlias(true);
        mPaintStroke.setStyle(Paint.Style.STROKE);

        mPaintFillOutSize = new Paint();
        mPaintFillOutSize.setColor(Color.parseColor("#FEFAFA"));
        mPaintFillOutSize.setAntiAlias(true);
        mPaintFillOutSize.setStyle(Paint.Style.FILL);

        mPaintStrokeOutSize = new Paint();
        mPaintStrokeOutSize.setColor(Color.parseColor("#C8CCD7"));
        mPaintStrokeOutSize.setAntiAlias(true);
        mPaintStrokeOutSize.setStyle(Paint.Style.STROKE);
        mPaintStrokeOutSize.setStrokeWidth(2);

        //ClearPaint
        mPaintClear = new Paint();
        mPaintClear.setAlpha(0);
        mPaintClear.setColor(Color.BLACK);
        mPaintClear.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        mPaintClear.setStyle(Paint.Style.FILL);
        mPaintClear.setAntiAlias(true);

        //TextColor
        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setTextSize(mInnerTextSize);
        mPaintText.setColor(mInnerTextColor);
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setFakeBoldText(false);
        mPaintText.setTypeface(Typeface.SANS_SERIF);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int resultWidth;
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);

        if (modeWidth == MeasureSpec.EXACTLY) {
            resultWidth = sizeWidth;
        } else {
            resultWidth = mDefaultWidth;
            if (modeWidth == MeasureSpec.AT_MOST) {
                resultWidth = Math.min(resultWidth, sizeWidth);
            }
        }

        int resultHeight;
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (modeHeight == MeasureSpec.EXACTLY) {
            resultHeight = sizeHeight;
        } else {
            resultHeight = mDefaultHeight;
            if (modeHeight == MeasureSpec.AT_MOST) {
                resultHeight = Math.min(resultHeight, sizeHeight);
            }
        }

        setMeasuredDimension(resultWidth, resultHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(mCenterX, mCenterY, mRadarRadius + mBorderWidth, mPaintFillOutSize);
        canvas.drawCircle(mCenterX, mCenterY, mRadarRadius + mBorderWidth, mPaintStrokeOutSize);

        canvas.drawCircle(mCenterX, mCenterY, mRadarRadius, mPaintCircle);

        canvas.drawCircle(mCenterX, mCenterY, 3 * mRadarRadius / 5, mPaintInnerCircle);
        if (isScanning) {
//            canvas.drawCircle(mCenterX, mCenterY, mRadarRadius / 5, mPaintStroke);
            canvas.drawCircle(mCenterX, mCenterY, 2 * mRadarRadius / 5, mPaintStroke);
            canvas.drawCircle(mCenterX, mCenterY, 3 * mRadarRadius / 5, mPaintStroke);
        }

        canvas.drawCircle(mCenterX, mCenterY, 4 * mRadarRadius / 5, mPaintStroke);

        if (isScanning) {
            canvas.drawLine(mCenterX, mCenterY - mRadarRadius, mCenterX, mCenterY + mRadarRadius, mPaintStroke);
            canvas.drawLine(mCenterX - mRadarRadius, mCenterY, mCenterX + mRadarRadius, mCenterY, mPaintStroke);
        }

        if (mIsPutWhiteLayer) {
            canvas.drawBitmap(mLayerBitmap, mCenterX - mRadarRadius, mCenterY - mRadarRadius, null);
        }

        if (mIsClearing) {
            Log.i("Radar ", mStartClearDegree + "");
            mLayerCanvas.drawArc(mClearRect, 270, mStartClearDegree, true, mPaintClear);
        }

        if (mIsShowText) {
//            canvas.drawText(getShowNum(), mCenterX, mTextY, mPaintText);
            canvas.drawText(mCenterText, mCenterX, mTextY, mPaintText);
        }

        if (isScanning && !mIsClearing) {
            SweepGradient shader = new SweepGradient(mCenterX, mCenterY, mShaderColor1, mShaderColor2);
            mPaintRadar.setShader(shader);
            canvas.concat(mScanMatrix);
            canvas.drawLine(mCenterX, mCenterY, mCenterX + mRadarRadius, mCenterY, mPaintRadarLine);
            canvas.drawCircle(mCenterX, mCenterY, mRadarRadius, mPaintRadar);
        }

    }

    private String getShowNum() {
        String num;
        if (mCollectionNum > 0 && mCollectionNum < 100) {
            BigDecimal bd = new BigDecimal(mCollectionNum);
            bd = bd.setScale(1, RoundingMode.HALF_UP);
            num = bd.toString() + mUnit;
        } else if (mCollectionNum > 100) {
            num = ((int) mCollectionNum) + mUnit;
        } else {
            num = ((int) mCollectionNum) + mUnit;
        }

        return num;
    }

    private int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public void startScan() {
        isScanning = true;
        post(mScanRunnable);
    }

    public void stopScan() {
        isScanning = false;
    }

    public void startClear() {
        if (mIsPutWhiteLayer && mStartClearDegree > -360) {
            mPieceOfNum = mCollectionNum / 360;
            mIsClearing = true;
            post(mClearRun);
        }
    }

    public void stopClear() {
        mIsClearing = false;
    }

    public boolean isPutWhiteLayer() {
        return mIsPutWhiteLayer;
    }

    public void setWhiteLayer(boolean putWhiteLayer) {
        mIsPutWhiteLayer = putWhiteLayer;
        postInvalidate();
    }

    public int getClearTime() {
        return mClearTime;
    }

    public void setClearTime(int clearTime) {
        this.mClearTime = clearTime;
    }

    public double getCollectionNum() {
        return mCollectionNum;
    }

    public void setCollectionNum(double collectionNum) {
        this.mCollectionNum = collectionNum;
    }

    public String getUnit() {
        return mUnit;
    }

    public void setUnit(String unit) {
        this.mUnit = unit;
    }

    public void setShaderColor(int color1, int color2) {
        mShaderColor1 = color1;
        mShaderColor2 = color2;
    }

    public int getInnerTextColor() {
        return mInnerTextColor;
    }

    public void setInnerTextColor(int innerTextColor) {
        this.mInnerTextColor = innerTextColor;
    }

    public float getInnerTextSize() {
        return mInnerTextSize;
    }

    public void setInnerTextSize(int innerTextSize) {
        this.mInnerTextSize = innerTextSize;
    }

    public int getLayerColor() {
        return mLayerColor;
    }

    public void setLayerColor(int layerColor) {
        this.mLayerColor = layerColor;
    }

    public int getInnerCircleColor() {
        return mInnerCircleColor;
    }

    public void setInnerCircleColor(int innerCircleColor) {
        this.mInnerCircleColor = innerCircleColor;
    }

    public int getCircleColor() {
        return mCircleColor;
    }

    public void setCircleColor(int circleColor) {
        this.mCircleColor = circleColor;
    }

    public int getRadarLineColor() {
        return mRadarLineColor;
    }

    public void setRadarLineColor(int radarLineColor) {
        this.mRadarLineColor = radarLineColor;
    }

    public boolean isShowText() {
        return mIsShowText;
    }

    public void setShowText(boolean showText) {
        mIsShowText = showText;
    }

    public String getCenterText() {
        return mCenterText;
    }

    public void setCenterText(String centerText) {
        mCenterText = centerText;
    }
}
