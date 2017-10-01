package com.android.rdc.mobilesafe.ui;

import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseToolBarActivity;
import com.android.rdc.mobilesafe.dao.BlackNumberDao;
import com.android.rdc.mobilesafe.entity.BlackContactInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SecurityPhoneActivity extends BaseToolBarActivity {

    @BindView(R.id.iv_no_black_number)
    ImageView mIvNoBlackNumber;
    @BindView(R.id.tv_indicator)
    TextView mTvIndicator;
    @BindView(R.id.rv_black_list)
    RecyclerView mRvBlackList;
    @BindView(R.id.btn_add_black_number)
    Button mBtnAddBlackNumber;

    private BlackNumberDao mBlackNumberDao;
    private List<BlackContactInfo> mBlackContactInfoList = new ArrayList<>();

    @Override
    protected int setResId() {
        return R.layout.activity_security_phone;
    }

    @Override
    protected void initData() {
        //如有黑名单，则显示黑名单列表，没有就直接使用黑名单
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }
}
