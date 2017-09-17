package com.android.rdc.mobilesafe;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.rdc.mobilesafe.base.BaseActivity;
import com.android.rdc.mobilesafe.base.BaseRvAdapter;
import com.android.rdc.mobilesafe.ch01.adapter.HomeRvAdapter;
import com.android.rdc.mobilesafe.ch01.entitiy.HomeDataModel;
import com.android.rdc.mobilesafe.ch01.entitiy.HomeItem;
import com.android.rdc.mobilesafe.ch09.AppLockActivityBase;
import com.android.rdc.mobilesafe.ch10.SettingActivityBase;
import com.android.rdc.mobilesafe.ui.AppManagerActivity;

import java.util.List;

import butterknife.BindView;

public class HomeActivity extends BaseActivity {


    @BindView(R.id.rv_home)
    RecyclerView mRvHome;

    private List<HomeItem> mHomeItemList;
    private HomeRvAdapter homeRvAdapter;

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
        homeRvAdapter = new HomeRvAdapter();
        homeRvAdapter.setDataList(mHomeItemList);
        mRvHome.setAdapter(homeRvAdapter);
        mRvHome.setLayoutManager(new GridLayoutManager(this, 3));
    }

    @Override
    protected void initListener() {
        homeRvAdapter.setOnRvItemClickListener(new BaseRvAdapter.OnRvItemClickListener() {
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
                break;
            case R.drawable.app://软件管家
                break;
            case R.drawable.trojan://病毒查杀
                break;
            case R.drawable.sysoptimize://缓存优化
                break;
            case R.drawable.taskmanager://进程管理
                startActivity(AppManagerActivity.class);
                break;
            case R.drawable.netmanager://流量管理
                break;
            case R.drawable.atools://高级工具
                startActivity(AppLockActivityBase.class);
                break;
            case R.drawable.settings://设置
                startActivity(SettingActivityBase.class);
                break;
        }
    }

}
