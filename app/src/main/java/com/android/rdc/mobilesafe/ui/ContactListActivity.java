package com.android.rdc.mobilesafe.ui;

import android.support.v7.widget.RecyclerView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseToolBarActivity;
import com.android.rdc.mobilesafe.ui.widget.WaveSideBarView;

import butterknife.BindView;

public class ContactListActivity extends BaseToolBarActivity {


    @BindView(R.id.rv)
    RecyclerView mRv;
    @BindView(R.id.wave_side_bar_view)
    WaveSideBarView mWaveSideBarView;

    @Override
    protected int setResId() {
        return R.layout.activity_contact_list;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

}
