package com.android.rdc.mobilesafe.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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

/**
 * 骚扰拦截
 */
public class InterceptActivity extends BaseToolBarActivity {

    @BindView(R.id.iv_no_black_number)
    ImageView mIvNoBlackNumber;
    @BindView(R.id.tv_indicator)
    TextView mTvIndicator;
    @BindView(R.id.rv_black_list)
    RecyclerView mRvBlack;

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
        //如有黑名单，则显示黑名单列表，没有就显示黑名单为空
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
        setTitle("黑名单号码");
    }

    @Override
    protected void initListener() {

    }


    @OnClick({R.id.iv_edit, R.id.iv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_edit:
                // TODO: 2017/11/1 0001 批量删除
                break;
            case R.id.iv_add:
                // TODO: 2017/11/1 0001 手动输入或者从联系人中添加
                showBottomSheetDialog();
                break;
        }
    }

    private void showBottomSheetDialog() {
        new AlertDialog.Builder(this)
                .setItems(new String[]{"手动输入", "从联系人中选择"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:

                                break;
                            case 1:
                                startActivity(ContactListActivity.class);
                                break;
                        }
                    }
                })
                .show();
    }


}
