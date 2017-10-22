package com.android.rdc.mobilesafe.adapter;

import android.content.Context;
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
import com.android.rdc.mobilesafe.entity.AppInfo;
import com.android.rdc.mobilesafe.util.ManagerSoftwareUtils;

import butterknife.BindView;

public class SoftwareManagerAdapter extends BaseRvAdapter<AppInfo> {
    private Context mContext;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_software_manager, parent, false);
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
//                    Glide.with(mContext).load(appInfo.getIcon()).into(mIvAppIcon);
                    mIvAppIcon.setImageDrawable(appInfo.getIcon());
                }
                mTvAppName.setText(appInfo.getName());
                mTvAppSize.setText(Formatter.formatFileSize(mContext, appInfo.getAppSize()));
                mTvInstallArea.setText(appInfo.getAppLocation());
                if (appInfo.isSelected() && mLlOperation == null) {//显示下方的「操作栏」
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
                    super.onClick(v);//整个列表项的默认点击，定义在父类
            }

        }
    }
}
