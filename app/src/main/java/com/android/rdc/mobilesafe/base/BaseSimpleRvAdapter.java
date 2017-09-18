package com.android.rdc.mobilesafe.base;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public abstract class BaseSimpleRvAdapter<T> extends RecyclerView.Adapter {

    protected List<T> mDataList = new ArrayList<>();
    protected OnRvItemClickListener mOnRvItemClickListener;
    protected OnRvItemLongClickListener mOnRvItemLongClickListener;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(setLayoutId(), parent, false);
        return createConcreteViewHolder(v);
    }

    protected abstract int setLayoutId();

    protected abstract BaseRvHolder createConcreteViewHolder(View view);

    public void setOnRvItemClickListener(OnRvItemClickListener onRvItemClickListener) {
        mOnRvItemClickListener = onRvItemClickListener;
    }

    public void setOnRvItemLongClickListener(OnRvItemLongClickListener onRvItemLongClickListener) {
        mOnRvItemLongClickListener = onRvItemLongClickListener;
    }

    public void setDataList(List<T> dataList) {
        mDataList = dataList;
    }

    public void appendData(List<T> dataList) {
        if (dataList != null && !dataList.isEmpty()) {
            mDataList.addAll(dataList);
        }
    }

    public List<T> getDataList() {
        return mDataList;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((BaseRvHolder) holder).bindView(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    /**
     * ViewHolder 的基类，内部实现了点击的回调，子类不需要再写重复的 setClick 方法
     */
    public abstract class BaseRvHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public BaseRvHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        /**
         * 绑定数据集 与 View
         */
        protected abstract void bindView(T t);

        @Override
        public void onClick(View v) {
            if (mOnRvItemClickListener != null) {
                mOnRvItemClickListener.onClick(getLayoutPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            //使用了 getAdapterPosition() 如果实时性要求高的话，需要改用 getLayoutPosition
            return mOnRvItemLongClickListener != null && mOnRvItemLongClickListener.onLongClick(getAdapterPosition());
        }
    }

    public interface OnRvItemClickListener {
        void onClick(int position);
    }

    public interface OnRvItemLongClickListener {
        boolean onLongClick(int position);
    }
}
