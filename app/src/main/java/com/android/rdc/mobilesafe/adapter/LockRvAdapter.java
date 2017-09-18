package com.android.rdc.mobilesafe.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseRvAdapter;
import com.android.rdc.mobilesafe.ch09.entity.AppInfo;

import butterknife.BindView;

public class LockRvAdapter extends BaseRvAdapter<AppInfo> {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_applock, parent, false);
        return new LockRvHolder2(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((LockRvHolder2) holder).bindView(mDataList.get(position));
    }


    class LockRvHolder2 extends BaseRvHolder {

        @BindView(R.id.iv_app_icon)
        ImageView mIvAppIcon;
        @BindView(R.id.tv_app_name)
        TextView mTvAppName;
        @BindView(R.id.iv_lock)
        ImageView mIvLock;

        public LockRvHolder2(View itemView) {
            super(itemView);
        }

        @Override
        protected void bindView(AppInfo appInfo) {
            mIvAppIcon.setImageDrawable(appInfo.mIcon);
            mTvAppName.setText(appInfo.mAppName);

            if (appInfo.isLock) {
                mIvLock.setBackgroundResource(R.drawable.applock_icon);
            } else {
                mIvLock.setBackgroundResource(R.drawable.app_unlock_icon);
            }
        }
    }

//    class LockViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        @BindView(R.id.iv_app_icon)
//        ImageView mIvAppIcon;
//        @BindView(R.id.tv_app_name)
//        TextView mTvAppName;
//        @BindView(R.id.iv_lock)
//        ImageView mIvLock;
//
//        public LockViewHolder(View itemView) {
//            super(itemView);
////            ButterKnife.bind(this, itemView);
//            itemView.setOnClickListener(this);
//        }
//
//        void bindView(AppInfo appInfo) {
//            mIvAppIcon.setImageDrawable(appInfo.mIcon);
//            mTvAppName.setText(appInfo.mAppName);
//
//            if (appInfo.isLock) {
//                mIvLock.setBackgroundResource(R.drawable.applock_icon);
//            } else {
//                mIvLock.setBackgroundResource(R.drawable.app_unlock_icon);
//            }
//        }
//
//        @Override
//        public void onClick(View v) {
//            if (mOnRvItemClickListener != null) {
//                mOnRvItemClickListener.onClick(getAdapterPosition());
//            }
//        }
//    }


}
