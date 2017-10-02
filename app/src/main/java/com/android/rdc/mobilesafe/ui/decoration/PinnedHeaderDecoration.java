package com.android.rdc.mobilesafe.ui.decoration;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

//确定类型，计算偏移
public class PinnedHeaderDecoration extends RecyclerView.ItemDecoration {
    private int mHeaderPosition;//头部位置
    private int mPinnedHeaderTop;//固定头部

    private boolean mIsAdapterDataChanged;//数据是否改变

    private Rect mClipBounds;//裁剪范围
    private View mPinnedHeaderView;//头部 View
    private RecyclerView.Adapter mAdapter;//适配器

    private final SparseArray<PinnedHeaderCreator> mTypePinnedHeaderFactories = new SparseArray<>();//
    private final RecyclerView.AdapterDataObserver mAdapterDataObserver = new RecyclerView.AdapterDataObserver() {//数据集观察者
        @Override
        public void onChanged() {
            mIsAdapterDataChanged = true;
        }
    };

    public PinnedHeaderDecoration() {
        this.mHeaderPosition = -1;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        createPinnedHeader(parent);

        if (mPinnedHeaderView != null) {
            int headerEndAt = mPinnedHeaderView.getTop() + mPinnedHeaderView.getHeight();
            View v = parent.findChildViewUnder(c.getWidth() / 2, headerEndAt + 1);

            if (isPinnedView(parent, v)) {
                mPinnedHeaderTop = v.getTop() - mPinnedHeaderView.getHeight();
            } else {
                mPinnedHeaderTop = 0;
            }

            mClipBounds = c.getClipBounds();
            mClipBounds.top = mPinnedHeaderTop + mPinnedHeaderView.getHeight();
            c.clipRect(mClipBounds);
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mPinnedHeaderView != null) {
            c.save();

            mClipBounds.top = 0;
            c.clipRect(mClipBounds, Region.Op.UNION);
            c.translate(0, mPinnedHeaderTop);
            mPinnedHeaderView.draw(c);

            c.restore();//重新恢复
        }
    }

    private void createPinnedHeader(RecyclerView parent) {
        updatePinnedHeader(parent);

        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();//获取布局管理器
        if (layoutManager == null || layoutManager.getChildCount() <= 0) {
            return;
        }
        int firstVisiblePosition = ((RecyclerView.LayoutParams) layoutManager.getChildAt(0)
                .getLayoutParams()).getViewAdapterPosition();
        int headerPosition = findPinnedHeaderPosition(parent, firstVisiblePosition);//查找固定位置的头部位置

        if (headerPosition >= 0 && mHeaderPosition != headerPosition) {//
            mHeaderPosition = headerPosition;
            int viewType = mAdapter.getItemViewType(headerPosition);

            RecyclerView.ViewHolder pinnedViewHolder = mAdapter.createViewHolder(parent, viewType);
            mAdapter.bindViewHolder(pinnedViewHolder, headerPosition);
            mPinnedHeaderView = pinnedViewHolder.itemView;

            // read layout parameters
            ViewGroup.LayoutParams layoutParams = mPinnedHeaderView.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mPinnedHeaderView.setLayoutParams(layoutParams);
            }

            int heightMode = View.MeasureSpec.getMode(layoutParams.height);
            int heightSize = View.MeasureSpec.getSize(layoutParams.height);

            if (heightMode == View.MeasureSpec.UNSPECIFIED) {
                heightMode = View.MeasureSpec.EXACTLY;
            }

            int maxHeight = parent.getHeight() - parent.getPaddingTop() - parent.getPaddingBottom();
            if (heightSize > maxHeight) {
                heightSize = maxHeight;
            }

            // measure & layout
            int ws = View.MeasureSpec.makeMeasureSpec(parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight(), View.MeasureSpec.EXACTLY);
            int hs = View.MeasureSpec.makeMeasureSpec(heightSize, heightMode);
            mPinnedHeaderView.measure(ws, hs);
            mPinnedHeaderView.layout(0, 0, mPinnedHeaderView.getMeasuredWidth(), mPinnedHeaderView.getMeasuredHeight());
        }
    }

    private int findPinnedHeaderPosition(RecyclerView parent, int fromPosition) {
        if (fromPosition > mAdapter.getItemCount() || fromPosition < 0) {
            return -1;
        }

        for (int position = fromPosition; position >= 0; position--) {
            final int viewType = mAdapter.getItemViewType(position);//获取列表项 View 类型
            if (isPinnedViewType(parent, position, viewType)) {//列表项类型判断
                return position;
            }
        }

        return -1;
    }

    private boolean isPinnedViewType(RecyclerView parent, int adapterPosition, int viewType) {
        PinnedHeaderCreator pinnedHeaderCreator = mTypePinnedHeaderFactories.get(viewType);

        return pinnedHeaderCreator != null && pinnedHeaderCreator.create(parent, adapterPosition);
    }

    private boolean isPinnedView(RecyclerView parent, View v) {
        int position = parent.getChildAdapterPosition(v);
        if (position == RecyclerView.NO_POSITION) {
            return false;
        }

        return isPinnedViewType(parent, position, mAdapter.getItemViewType(position));
    }

    private void updatePinnedHeader(RecyclerView parent) {
        RecyclerView.Adapter adapter = parent.getAdapter();
        if (mAdapter != adapter || mIsAdapterDataChanged) {
            resetPinnedHeader();
            if (mAdapter != null) {
                mAdapter.unregisterAdapterDataObserver(mAdapterDataObserver);
            }

            mAdapter = adapter;
            if (mAdapter != null) {
                mAdapter.registerAdapterDataObserver(mAdapterDataObserver);
            }
        }
    }

    private void resetPinnedHeader() {
        mHeaderPosition = -1;
        mPinnedHeaderView = null;
    }

    public void registerTypePinnedHeader(int itemType, PinnedHeaderCreator pinnedHeaderCreator) {
        mTypePinnedHeaderFactories.put(itemType, pinnedHeaderCreator);
    }

    public interface PinnedHeaderCreator {
        boolean create(RecyclerView parent, int adapterPosition);
    }
}
