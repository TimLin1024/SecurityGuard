package com.android.rdc.mobilesafe.ui;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.adapter.BlackNumberAdapter;
import com.android.rdc.mobilesafe.base.BaseToolBarActivity;
import com.android.rdc.mobilesafe.dao.BlackNumberDao;
import com.android.rdc.mobilesafe.entity.BlackContactInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SecurityPhoneActivity extends BaseToolBarActivity {

    @BindView(R.id.iv_no_black_number)
    ImageView mIvNoBlackNumber;
    @BindView(R.id.tv_indicator)
    TextView mTvIndicator;
    @BindView(R.id.rv_black_list)
    RecyclerView mRvBlack;
    @BindView(R.id.btn_add_black_number)
    Button mBtnAddBlackNumber;

    private BlackNumberDao mBlackNumberDao;
    private List<BlackContactInfo> mBlackContactInfoList = new ArrayList<>();
    private BlackNumberAdapter mAdapter;

    @Override
    protected int setResId() {
        return R.layout.activity_security_phone;
    }

    @Override
    protected void initData() {
        mBlackNumberDao = new BlackNumberDao(getApplicationContext());

        for (int i = 0; i < 100; i++) {
            BlackContactInfo blackContactInfo = new BlackContactInfo();
            blackContactInfo.setContractName(String.valueOf(i));
            blackContactInfo.setMode(1);
            blackContactInfo.setPhoneNumber(String.valueOf(i * 1024));
            mBlackNumberDao.add(blackContactInfo);
        }

        int totalNum = mBlackNumberDao.getTotalNumber();
        mBlackContactInfoList.addAll(mBlackNumberDao.getPagesBlackNumber(0, 150));
        //如有黑名单，则显示黑名单列表，没有就直接使用黑名单
        if (totalNum > 0) {
            mRvBlack.setVisibility(View.VISIBLE);
            mTvIndicator.setVisibility(View.GONE);
            mIvNoBlackNumber.setVisibility(View.GONE);
            if (mAdapter == null) {
                mAdapter = new BlackNumberAdapter();
                mAdapter.setDataList(mBlackContactInfoList);
                final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                mRvBlack.setLayoutManager(layoutManager);
                mRvBlack.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
                mRvBlack.setAdapter(mAdapter);

                mRvBlack.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            int lastVisiblePos = layoutManager.findLastVisibleItemPosition();

                        }
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                    }
                });
            } else {
                mAdapter.notifyDataSetChanged();
            }
        } else {
            mRvBlack.setVisibility(View.GONE);
            mTvIndicator.setVisibility(View.VISIBLE);
            mIvNoBlackNumber.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

    @OnClick(R.id.btn_add_black_number)
    public void onViewClicked() {
        startActivity(AddBlackNumberActivity.class);
    }
}
