package com.android.rdc.mobilesafe.ui;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseToolBarActivity;

public class EnterBlackNumberActivity extends BaseToolBarActivity {


    @Override
    protected int setResId() {
        return R.layout.activity_enter_black_number;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        //不显示后退箭头
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    protected void initListener() {

    }
}
