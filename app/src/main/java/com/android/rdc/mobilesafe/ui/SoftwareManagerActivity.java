package com.android.rdc.mobilesafe.ui;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.adapter.ViewPagerAdapter;
import com.android.rdc.mobilesafe.base.BaseToolBarActivity;
import com.android.rdc.mobilesafe.service.CleanBackgroundProcessAccessibilityService;
import com.android.rdc.mobilesafe.ui.fragment.SoftwareManagerFragment;

import butterknife.BindView;

public class SoftwareManagerActivity extends BaseToolBarActivity {

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private String[] mTitles = {"用户应用", "系统应用"};
    private Fragment[] mFragments = new Fragment[2];

    @Override
    protected int setResId() {
        return R.layout.activity_software_manager;
    }

    @Override
    protected void initData() {
        mTabLayout.addTab(mTabLayout.newTab()/*.setText(mTitles[0])*/);
        mTabLayout.addTab(mTabLayout.newTab()/*.setText(mTitles[1])*/);
        mFragments[0] = SoftwareManagerFragment.newInstance(SoftwareManagerFragment.DISPLAY_USER_APP);
        mFragments[1] = SoftwareManagerFragment.newInstance(SoftwareManagerFragment.DISPLAY_SYSTEM_APP);

        mViewPager.setOffscreenPageLimit(0);
        bindCleanProcessService();
    }

    @Override
    protected void initView() {
        setTitle("软件管理");
        mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), this, mFragments, mTitles));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void initListener() {

    }

    private void bindCleanProcessService() {
        Intent intent = new Intent(this, CleanBackgroundProcessAccessibilityService.class);

//        bindService(intent, new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName name, IBinder service) {
//                showToast("onServiceConnected");
//            }
//
//            @Override
//            public void onServiceDisconnected(ComponentName name) {
//                Log.d(TAG, "onServiceDisconnected: ");
//            }
//        }, BIND_AUTO_CREATE);
        startService(intent);
    }

}
