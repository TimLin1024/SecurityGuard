package com.android.rdc.mobilesafe.adapter;

import android.text.format.Formatter;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseSimpleRvAdapter;
import com.android.rdc.mobilesafe.bean.CacheInfo;

import butterknife.BindView;

public class CleanCacheAdapter extends BaseSimpleRvAdapter<CacheInfo> {
    @Override
    protected int setLayoutId() {
        return R.layout.item_cache_list;
    }

    @Override
    protected BaseRvHolder createConcreteViewHolder(View view) {
        return new CacheVH(view);
    }

    class CacheVH extends BaseRvHolder {
        @BindView(R.id.iv_icon)
        ImageView mIvIcon;
        @BindView(R.id.tv_app_name)
        TextView mTvAppName;
        @BindView(R.id.tv_cache_size)
        TextView mTvCacheSize;
        @BindView(R.id.cb)
        CheckBox mCb;

        public CacheVH(View itemView) {
            super(itemView);
        }

        @Override
        protected void bindView(CacheInfo cacheInfo) {
            mIvIcon.setImageDrawable(cacheInfo.getIcon());
            mTvAppName.setText(cacheInfo.getAppName());
            mTvCacheSize.setText(Formatter.formatFileSize(mIvIcon.getContext(), cacheInfo.getCacheSize()));
            mCb.setChecked(cacheInfo.isSelected());
        }
    }
}
