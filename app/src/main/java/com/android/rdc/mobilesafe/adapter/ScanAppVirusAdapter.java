package com.android.rdc.mobilesafe.adapter;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseSimpleRvAdapter;
import com.android.rdc.mobilesafe.entity.ScanAppInfo;

import butterknife.BindView;

public class ScanAppVirusAdapter extends BaseSimpleRvAdapter<ScanAppInfo> {



    @Override
    protected int setLayoutId() {
        return R.layout.item_list_applock;
    }

    @Override
    protected BaseRvHolder createConcreteViewHolder(View view) {
        return new ScanVirusHolder(view);
    }

    class ScanVirusHolder extends BaseRvHolder {
        /**
         * 因为列表项与程序锁的列表项相似，因此直接复用程序锁的列表项布局
         */
        @BindView(R.id.iv_app_icon)
        ImageView mIvAppIcon;
        @BindView(R.id.tv_app_name)
        TextView mTvAppName;
        @BindView(R.id.iv_lock)
        ImageView mIvLock;

        public ScanVirusHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void bindView(ScanAppInfo scanAppInfo) {
            mIvAppIcon.setImageDrawable(scanAppInfo.getIcon());

            if (scanAppInfo.isVirus()) {
                mTvAppName.setTextColor(Color.RED);
                mTvAppName.setText(scanAppInfo.getAppName() + "(" + scanAppInfo.getDescription() + ")");
            } else {
                mIvLock.setImageResource(R.drawable.blue_right_icon);
                mTvAppName.setText(scanAppInfo.getAppName());
            }
        }
    }

}
