package com.android.rdc.mobilesafe.ch04appmanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseRvAdapter;
import com.android.rdc.mobilesafe.ch04appmanager.entity.AppInfo;
import com.android.rdc.mobilesafe.ch04appmanager.util.EngineUtils;

import butterknife.BindView;

public class AppManagerAdapter extends BaseRvAdapter<AppInfo> {
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_manager, parent, false);
        return new AppManagerHolder(v);
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


        public AppManagerHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void bindView(AppInfo appInfo) {
            mAppInfo = appInfo;
            if (appInfo != null) {
                if (appInfo.getIcon() != null) {
                    mIvAppIcon.setImageDrawable(appInfo.getIcon());
                }
                mTvAppName.setText(appInfo.getName());
                if (appInfo.isSelected() && mLlOperation == null) {
                    View v = mViewStub.inflate();
                    mLlOperation = (LinearLayout) v.findViewById(R.id.ll_operation);
                    LinearLayout llStartApp = (LinearLayout) v.findViewById(R.id.ll_start_app);
                    LinearLayout llUninstallApp = (LinearLayout) v.findViewById(R.id.ll_uninstall_app);
                    LinearLayout llShareApp = (LinearLayout) v.findViewById(R.id.ll_share_app);
                    LinearLayout llSettingApp = (LinearLayout) v.findViewById(R.id.ll_setting_app);

                    llStartApp.setOnClickListener(this);
                    llUninstallApp.setOnClickListener(this);
                    llShareApp.setOnClickListener(this);
                    llSettingApp.setOnClickListener(this);
                } else if (mLlOperation != null) {
                    mLlOperation.setVisibility(View.GONE);
                }
//                mTvAppSize.setText(String.valueOf(appInfo.getAppSize() / 1024) + "M");
            }
        }

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            switch (v.getId()) {
                case R.id.ll_start_app:
                    EngineUtils.startApp(context, mAppInfo);
                    break;
                case R.id.ll_share_app:
                    EngineUtils.shareApp(context, mAppInfo);
                    break;
                case R.id.ll_setting_app:
                    EngineUtils.settingAppDetail(context, mAppInfo);
                    break;
                case R.id.ll_uninstall_app:
                    EngineUtils.uninstallApp(context, mAppInfo);
                    break;
                default:
                    super.onClick(v);
            }

        }
    }
}
