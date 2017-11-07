package com.android.rdc.mobilesafe.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseRvAdapter;
import com.android.rdc.mobilesafe.bean.AppInfo;
import com.android.rdc.mobilesafe.util.ManagerSoftwareUtils;

import butterknife.BindView;

public class SoftwareManagerAdapter extends BaseRvAdapter<AppInfo> {
    private Context mContext;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        if (viewType == 0) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tv, parent, false);
            return new TvHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_software_manager, parent, false);
            return new AppManagerHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position != 0) {
            super.onBindViewHolder(holder, position - 1);//当前位置减一，对应里列表数据
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;//直接返回位置，位置为 0 创建 TextView
    }

    @Override
    public int getItemCount() {
        return mDataList.size() + 1;
    }

    class AppManagerHolder extends BaseRvHolder {

        @BindView(R.id.iv_app_icon)
        ImageView mIvAppIcon;
        @BindView(R.id.tv_app_name)
        TextView mTvAppName;
        @BindView(R.id.tv_install_area)
        TextView mTvInstallArea;
        @BindView(R.id.tv_app_size)
        TextView mTvAppSize;
        @BindView(R.id.view_stub)
        ViewStub mViewStub;

        private AppInfo mAppInfo;
        private LinearLayout mLlOperation;

        AppManagerHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void bindView(AppInfo appInfo) {
            mAppInfo = appInfo;
            if (appInfo != null) {
                if (appInfo.getIcon() != null) {
//                    Glide.with(mContext).load(appInfo.getIcon()).into(mIvAppIcon);
                    mIvAppIcon.setImageDrawable(appInfo.getIcon());
                }
                mTvAppName.setText(appInfo.getName());
                mTvAppSize.setText(Formatter.formatFileSize(mContext, appInfo.getAppSize()));
                mTvInstallArea.setText(appInfo.getAppLocation());
                if (appInfo.isSelected()) {
                    if (mLlOperation == null) {
                        View v = mViewStub.inflate();
                        mLlOperation = (LinearLayout) v.findViewById(R.id.ll_operation);
                        TextView tvStartApp = (TextView) v.findViewById(R.id.tv_start_app);
                        TextView tvUninstallApp = (TextView) v.findViewById(R.id.tv_uninstall_app);
                        TextView tvShareApp = (TextView) v.findViewById(R.id.tv_share_app);
                        TextView tvSettingApp = (TextView) v.findViewById(R.id.tv_setting_app);

                        tvStartApp.setOnClickListener(this);
                        tvUninstallApp.setOnClickListener(this);
                        tvShareApp.setOnClickListener(this);
                        tvSettingApp.setOnClickListener(this);
                    } else {
                        mLlOperation.setVisibility(View.VISIBLE);
                    }
                } else if (mLlOperation != null) {
                    mLlOperation.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            switch (v.getId()) {
                case R.id.tv_start_app:
                    ManagerSoftwareUtils.startApp(context, mAppInfo);
                    break;
                case R.id.tv_share_app:
                    ManagerSoftwareUtils.shareApp(context, mAppInfo);
                    break;
                case R.id.tv_setting_app:
                    ManagerSoftwareUtils.settingAppDetail(context, mAppInfo);
                    break;
                case R.id.tv_uninstall_app:
                    ManagerSoftwareUtils.uninstallApp(context, mAppInfo);
                    break;
                default:
                    if (mOnRvItemClickListener != null) {
                        mOnRvItemClickListener.onClick(getLayoutPosition() - 1);
                    }
//                    super.onClick(v);//整个列表项的默认点击，定义在父类
            }
        }

    }

    private class TvHolder extends BaseRvHolder {

        TextView mTextView;

        TvHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.tv_free_mem);
            updateFreeMemoryOnPhone();
        }

        @Override
        protected void bindView(AppInfo appInfo) {

        }


        @SuppressLint("SetTextI18n")
        private void updateFreeMemoryOnPhone() {
            long freeSpace = Environment.getDataDirectory().getFreeSpace();
            mTextView.setText("剩余：" + Formatter.formatFileSize(mContext, freeSpace));
        }
    }


}
