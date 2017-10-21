package com.android.rdc.mobilesafe;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;

import com.android.rdc.mobilesafe.adapter.HomeRvAdapter;
import com.android.rdc.mobilesafe.base.BaseActivity;
import com.android.rdc.mobilesafe.base.BaseRvAdapter;
import com.android.rdc.mobilesafe.entity.CustomEvent;
import com.android.rdc.mobilesafe.entity.HomeDataModel;
import com.android.rdc.mobilesafe.entity.HomeItem;
import com.android.rdc.mobilesafe.ui.AppLockActivityBase;
import com.android.rdc.mobilesafe.ui.AppManagerActivity;
import com.android.rdc.mobilesafe.ui.CacheListActivity;
import com.android.rdc.mobilesafe.ui.OperatorSettingActivity;
import com.android.rdc.mobilesafe.ui.ProcessManagerActivity;
import com.android.rdc.mobilesafe.ui.ScanActivity;
import com.android.rdc.mobilesafe.ui.SecurityPhoneActivity;
import com.android.rdc.mobilesafe.ui.TrafficMonitoringActivity;
import com.android.rdc.mobilesafe.ui.widget.GridDividerItemDecoration;
import com.android.rdc.mobilesafe.ui.widget.RoundProgress;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;

public class HomeActivity extends BaseActivity {


    @BindView(R.id.rv_home)
    RecyclerView mRvHome;
    @BindView(R.id.round_progress)
    RoundProgress mRoundProgress;

    private static final int MSG_UPDATE_PROGRESS = 101;
    private int mCurrentProgress;

    private List<HomeItem> mHomeItemList;
    private HomeRvAdapter mHomeRvAdapter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_PROGRESS:
                    if (mCurrentProgress < 100) {
                        mCurrentProgress += 5;
                        if (mCurrentProgress > 100) {
                            mCurrentProgress = 100;
                        }

                        mRoundProgress.updateProcess(mCurrentProgress);
                        this.sendEmptyMessageAtTime(MSG_UPDATE_PROGRESS, 200);
                    }
                    break;
            }
        }
    };

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
        mRvHome.setLayoutManager(new GridLayoutManager(this, 1, LinearLayoutManager.HORIZONTAL, false));
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
            case R.drawable.safe://防盗

                break;
            case R.drawable.callmsgsafe://通讯卫士
                startActivity(SecurityPhoneActivity.class);
                break;
            case R.drawable.app://软件管家
                startActivity(AppManagerActivity.class);
                break;
            case R.drawable.menu_icon_virus_save_normal://病毒查杀
                startActivity(ScanActivity.class);
                break;
            case R.drawable.sysoptimize://缓存优化
                startActivity(CacheListActivity.class);
                break;
            case R.drawable.taskmanager://进程管理
                startActivity(ProcessManagerActivity.class);
                break;
            case R.drawable.menu_icon_net_safe_normal://流量管理
                startActivity(TrafficMonitoringActivity.class);
                break;
            case R.drawable.atools://高级工具
                startActivity(AppLockActivityBase.class);
                break;
            case R.drawable.settings://设置
                startActivity(OperatorSettingActivity.class);

                EventBus.getDefault().post(new CustomEvent("自定义事件"));
                break;
        }
    }

}
