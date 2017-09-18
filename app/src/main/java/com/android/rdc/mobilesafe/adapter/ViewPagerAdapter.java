package com.android.rdc.mobilesafe.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private String[] mTabTitles;
    private Fragment[] mFragments;

    public ViewPagerAdapter(FragmentManager fm, Context context, Fragment[] fragmentList, String[] tabTitles) {
        super(fm);
        mTabTitles = tabTitles;
        mFragments = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments[position];
    }


    @Override
    public int getCount() {
        return mFragments.length;
    }

    /**
     * 标题
     * */
    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles[position];
    }
}
