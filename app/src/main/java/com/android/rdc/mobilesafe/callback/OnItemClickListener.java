package com.android.rdc.mobilesafe.callback;

import android.support.v7.widget.RecyclerView;

public abstract class OnItemClickListener {

    public void onItemLongClick(RecyclerView.ViewHolder vh, int position) {
    }

    abstract public void onItemClick(RecyclerView.ViewHolder vh, int position);
}
