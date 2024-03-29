package com.android.rdc.mobilesafe.ui.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import com.android.rdc.mobilesafe.ui.widget.BaseTagBean;

import java.util.List;

public class TitleItemDecoration extends RecyclerView.ItemDecoration {
    private List<? extends BaseTagBean> mDatas;
    private Paint mPaint;
    private Rect mBounds;//用于存放测量文字Rect

    private int mTitleHeight;
    private static final int COLOR_TITLE_BG = Color.parseColor("#E5E5E5");
    private static final int COLOR_TITLE_FONT = Color.parseColor("#FF000000");
    private static int mTitleFontSize;
    private static final int TITLE_PADDING = 20;

    public TitleItemDecoration(Context context, List<? extends BaseTagBean> datas) {
        mDatas = datas;
        mBounds = new Rect();
        mTitleHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());
        mTitleFontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, context.getResources().getDisplayMetrics());
        mPaint = new Paint();
        mPaint.setTextSize(mTitleFontSize);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        //我记得Rv的item position在重置时可能为-1.保险点判断一下吧
        if (position > -1) {
            if (position == 0) {//等于0肯定要有title的
                outRect.set(0, mTitleHeight, 0, 0);
            } else {//其他的通过判断
                if (null != mDatas.get(position).getTag() && !mDatas.get(position).getTag().equals(mDatas.get(position - 1).getTag())) {
                    outRect.set(0, mTitleHeight, 0, 0);//不为空 且跟前一个tag不一样了，说明是新的分类，也要title
                } else {
                    outRect.set(0, 0, 0, 0);
                }
            }
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int position = params.getViewLayoutPosition();//获取位置

            //我记得Rv的item position在重置时可能为-1.保险点判断一下吧
            if (position > -1) {
                if (position == 0) {//等于0肯定要有title的
                    drawTitleArea(c, left, right, child, params, position);

                } else {//其他的通过判断
                    if (null != mDatas.get(position).getTag() && !mDatas.get(position).getTag().equals(mDatas.get(position - 1).getTag())) {
                        //不为空 且跟前一个tag不一样了，说明是新的分类，也要title
                        drawTitleArea(c, left, right, child, params, position);
                    } else {
                        //none
                    }
                }
            }
        }
    }

    private void drawTitleArea(Canvas c, int left, int right, View child, RecyclerView.LayoutParams params, int position) {
        mPaint.setColor(COLOR_TITLE_BG);
        c.drawRect(left, child.getTop() - params.topMargin - mTitleHeight, right, child.getTop() - params.topMargin, mPaint);
        mPaint.setColor(COLOR_TITLE_FONT);
/*
        Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
        int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;*/

        mPaint.getTextBounds(mDatas.get(position).getTag(), 0, mDatas.get(position).getTag().length(), mBounds);
        c.drawText(mDatas.get(position).getTag(), child.getPaddingLeft() + TITLE_PADDING, child.getTop() - params.topMargin - (mTitleHeight / 2 - mBounds.height() / 2), mPaint);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
//        super.onDrawOver(c, parent, state);
        int pos = ((LinearLayoutManager) (parent.getLayoutManager())).findFirstVisibleItemPosition();//通过获取 rv 的布局管理器，进而或第一个可视布局

        String tag = mDatas.get(pos).getTag();//获取 tag
        //View child = parent.getChildAt(pos);
        View child = parent.findViewHolderForLayoutPosition(pos).itemView;//出现一个奇怪的bug，有时候child为空，所以将 child = parent.getChildAt(i)。-》 parent.findViewHolderForLayoutPosition(pos).itemView

        boolean flag = false;//定义一个flag，Canvas是否位移过的标志
        if ((pos + 1) < mDatas.size()) {//防止数组越界（一般情况不会出现）
            if (null != tag && !tag.equals(mDatas.get(pos + 1).getTag())) {//当前第一个可见的Item的tag，不等于其后一个item的tag，说明悬浮的View要切换了
                if (child.getHeight() + child.getTop() <= mTitleHeight) {//当第一个可见的item在屏幕中还剩的高度小于title区域的高度时，我们也该开始做悬浮Title的“交换动画”
                    c.save();//每次绘制前 保存当前Canvas的坐标状态，
                    flag = true;

                    //一种头部折叠起来的视效，个人觉得也还不错~
                    //可与123行 c.drawRect 比较，只有bottom参数不一样，由于 child.getHeight() + child.getTop() < mTitleHeight，所以绘制区域是在不断的减小，有种折叠起来的感觉
                    //c.clipRect(parent.getPaddingLeft(), parent.getPaddingTop(), parent.getRight() - parent.getPaddingRight(), parent.getPaddingTop() + child.getHeight() + child.getTop());


                    //上滑时，将canvas上移 （y为负数） ,所以后面 canvas 画出来的Rect和Text都上移了，有种切换的“动画”感觉
                    //绘制的位置不用改变，改变的是坐标系
                    c.translate(0, child.getHeight() + child.getTop() - mTitleHeight);
                }
            }
        }
        mPaint.setColor(COLOR_TITLE_BG);
        c.drawRect(parent.getPaddingLeft(), parent.getPaddingTop(),
                parent.getRight() - parent.getPaddingRight(), parent.getPaddingTop() + mTitleHeight,
                mPaint);
        mPaint.setColor(COLOR_TITLE_FONT);
        mPaint.getTextBounds(tag, 0, tag.length(), mBounds);

        c.drawText(tag, child.getPaddingLeft() + TITLE_PADDING,
                parent.getPaddingTop() + mTitleHeight - (mTitleHeight / 2 - mBounds.height() / 2),
                mPaint);
        if (flag)
            c.restore();//恢复画布到之前保存的状态

    }
}
