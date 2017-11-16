package com.android.rdc.mobilesafe;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.android.rdc.mobilesafe.adapter.HomeRvAdapter;
import com.android.rdc.mobilesafe.base.BaseActivity;
import com.android.rdc.mobilesafe.base.BaseRvAdapter;
import com.android.rdc.mobilesafe.base.BaseSafeActivityHandler;
import com.android.rdc.mobilesafe.bean.HomeDataModel;
import com.android.rdc.mobilesafe.bean.HomeItem;
import com.android.rdc.mobilesafe.ui.AppLockActivity;
import com.android.rdc.mobilesafe.ui.BlackNumListActivity;
import com.android.rdc.mobilesafe.ui.ProcessManagerActivity;
import com.android.rdc.mobilesafe.ui.ScanCacheActivity;
import com.android.rdc.mobilesafe.ui.ScanVirusActivity;
import com.android.rdc.mobilesafe.ui.SettingActivity;
import com.android.rdc.mobilesafe.ui.SoftwareManagerActivity;
import com.android.rdc.mobilesafe.ui.TrafficMonitoringActivity;
import com.android.rdc.mobilesafe.ui.widget.GridDividerItemDecoration;
import com.android.rdc.mobilesafe.ui.widget.RoundProgress;
import com.android.rdc.mobilesafe.ui.widget.RoundRectDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class HomeActivity extends BaseActivity {

    @BindView(R.id.rv_home)
    RecyclerView mRvHome;
    @BindView(R.id.round_progress)
    RoundProgress mRoundProgress;
    @BindView(R.id.iv_setting)
    ImageView mIvSetting;

    private static final int MSG_UPDATE_PROGRESS = 101;
    private int mCurrentProgress;
    private static final String KEY_EXIT_APP = "EXIT_APP";
    private List<HomeItem> mHomeItemList;
    private HomeRvAdapter mHomeRvAdapter;

    private static class SafeActivityHandler extends BaseSafeActivityHandler<HomeActivity> {

        SafeActivityHandler(HomeActivity activityReference) {
            super(activityReference);
        }

        @Override
        public void handleMessage(Message msg) {
            HomeActivity activity = getActivity();
            if (activity == null) {
                return;
            }

            switch (msg.what) {
                case MSG_UPDATE_PROGRESS:
                    if (activity.mCurrentProgress < 100) {
                        activity.mCurrentProgress += 5;
                        if (activity.mCurrentProgress > 100) {
                            activity.mCurrentProgress = 100;
                        }
                        activity.mRoundProgress.updateProcess(activity.mCurrentProgress);
                        this.sendEmptyMessageAtTime(MSG_UPDATE_PROGRESS, 200);
                    }
                    break;
            }
        }
    }

    private Handler mHandler = new SafeActivityHandler(this);

    public static Intent newIntent(Context context, boolean exitApp) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(KEY_EXIT_APP, exitApp);
        return intent;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //关闭应用逻辑判断
        if (intent != null) {
            boolean exitApp = intent.getBooleanExtra(KEY_EXIT_APP, false);
            if (exitApp) {
                finish();
            }
        }
    }

    @Override
    protected int setResId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initData() {
        mHomeItemList = HomeDataModel.getItemList();
    }

    @Override
    protected void initView() {
        mHomeRvAdapter = new HomeRvAdapter();
        mHomeRvAdapter.setDataList(mHomeItemList);
        mRvHome.setAdapter(mHomeRvAdapter);
        mRvHome.setLayoutManager(new GridLayoutManager(this, 3));
        mRvHome.addItemDecoration(new GridDividerItemDecoration(10, 10));

        LinearSnapHelper linearSnapHelper = new LinearSnapHelper();
        linearSnapHelper.attachToRecyclerView(mRvHome);

        mHandler.sendEmptyMessage(MSG_UPDATE_PROGRESS);
    }



    @Override
    protected void initListener() {
        mHomeRvAdapter.setOnRvItemClickListener(new BaseRvAdapter.OnRvItemClickListener() {
            @Override
            public void onClick(int position) {
                handleClick(position);
            }
        });
    }

    private void handleClick(int position) {
        HomeItem item = mHomeItemList.get(position);
        switch (item.getImgId()) {
            case R.drawable.ic_lock://程序锁
                startActivity(AppLockActivity.class);
                break;
            case R.drawable.ic_intercept://通讯卫士,骚扰拦截
                startActivity(BlackNumListActivity.class);
                break;
            case R.drawable.ic_software_manager://软件管家
                startActivity(SoftwareManagerActivity.class);
                break;
            case R.drawable.ic_scan_virus://病毒查杀
                startActivity(ScanVirusActivity.class);
                break;
            case R.drawable.ic_clean_cache://缓存清理
                startActivity(ScanCacheActivity.class);
                break;
            case R.drawable.ic_task_manager://进程管理
                startActivity(ProcessManagerActivity.class);
                break;
            case R.drawable.ic_traffic://流量管理
                startActivity(TrafficMonitoringActivity.class);
                break;
            case R.drawable.settings://设置
                new RoundRectDialog.Builder(this)
                        .setTitle("停止病毒扫描")
                        .setMsg("是否停止扫描？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", (dialog, which) -> {
                            finish();
                        })
                        .show();
                break;
        }
    }

    @OnClick(R.id.iv_setting)
    public void onViewClicked() {
        startActivity(SettingActivity.class);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {

    }
}
