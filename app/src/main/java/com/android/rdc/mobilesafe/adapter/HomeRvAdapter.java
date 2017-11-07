package com.android.rdc.mobilesafe.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseRvAdapter;
import com.android.rdc.mobilesafe.bean.HomeItem;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeRvAdapter extends BaseRvAdapter<HomeItem> {


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((HomeViewHolder) holder).bindView(mDataList.get(position));
    }

    class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.iv_icon)
        ImageView mIvIcon;
        @BindView(R.id.tv_item_name)
        TextView mTvItemName;

        public HomeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        void bindView(HomeItem homeItem) {
            mIvIcon.setImageResource(homeItem.getImgId());
            mTvItemName.setText(homeItem.getItemName());
        }

        @Override
        public void onClick(View v) {
            mOnRvItemClickListener.onClick(getAdapterPosition());
        }
    }

}