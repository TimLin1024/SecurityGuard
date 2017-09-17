package com.android.rdc.mobilesafe.ch09;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseToolBarActivity;
import com.android.rdc.mobilesafe.ch09.adapter.ViewPagerAdapter;

import butterknife.BindView;

public class AppLockActivityBase extends BaseToolBarActivity {

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;


    private String[] mTitles = {"未加锁", "已加锁"};
    private Fragment[] mFragments = new Fragment[2];

    @Override
    protected int setResId() {
        return R.layout.activity_app_lock;
    }

    @Override
    protected void initData() {
        mTabLayout.addTab(mTabLayout.newTab()/*.setText(mTitles[0])*/);
        mTabLayout.addTab(mTabLayout.newTab()/*.setText(mTitles[1])*/);
        mFragments[0] = UnLockFragment.newInstance();
        mFragments[1] = LockFragment.newInstance();

    }

    @Override
    protected void initView() {
        setTitle("程序锁");
        mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), this, mFragments, mTitles));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void initListener() {

    }
}
