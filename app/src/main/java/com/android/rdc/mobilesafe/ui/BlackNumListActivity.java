package com.android.rdc.mobilesafe.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.adapter.BlackNumberAdapter;
import com.android.rdc.mobilesafe.base.BaseSimpleRvAdapter;
import com.android.rdc.mobilesafe.base.BaseToolBarActivity;
import com.android.rdc.mobilesafe.callback.OnCheckedCountChangeListener;
import com.android.rdc.mobilesafe.dao.BlackNumberDao;
import com.android.rdc.mobilesafe.bean.BlackContactInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 骚扰拦截
 */
public class BlackNumListActivity extends BaseToolBarActivity {
    @BindView(R.id.iv_no_black_number)
    ImageView mIvNoBlackNumber;

    @BindView(R.id.tv_indicator)//黑名单为空的情况下显示的提示文字
            TextView mTvIndicator;
    @BindView(R.id.rv_black_list)
    RecyclerView mRvBlack;

    @BindView(R.id.tv_cancel)
    TextView mTvCancel;
    @BindView(R.id.tv_selected_count)
    TextView mTvSelectedCount;
    @BindView(R.id.tv_select_all)
    TextView mTvSelectAll;

    @BindView(R.id.ll_edit)
    LinearLayout mLlEdit;
    @BindView(R.id.ll_delete)
    LinearLayout mLlDelete;
    @BindView(R.id.ll_add)
    LinearLayout mLlAdd;

    private BlackNumberDao mBlackNumberDao;
    private List<BlackContactInfo> mBlackContactInfoList;
    private BlackNumberAdapter mAdapter;
    private int mTotalNum;
    private boolean mIsSelectAll = true;

    @Override
    protected int setResId() {
        return R.layout.activity_black_num_list;
    }

    @Override
    protected void initData() {
        mBlackNumberDao = new BlackNumberDao(getApplicationContext());
    }

    private void showBlackNumList(boolean showList) {
        if (showList) {
            mRvBlack.setVisibility(View.VISIBLE);
            mTvIndicator.setVisibility(View.GONE);
            mIvNoBlackNumber.setVisibility(View.GONE);
        } else {
            mRvBlack.setVisibility(View.GONE);
            mTvIndicator.setVisibility(View.VISIBLE);
            mIvNoBlackNumber.setVisibility(View.VISIBLE);
        }
    }

    private void initRvAndAdapter() {
        mAdapter = new BlackNumberAdapter();
        mAdapter.setDataList(mBlackContactInfoList);
        mAdapter.setHasStableIds(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRvBlack.setLayoutManager(layoutManager);
        mRvBlack.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRvBlack.setAdapter(mAdapter);
        mRvBlack.setHasFixedSize(true);
        setRvAdapterListener(layoutManager);

    }

    private void setRvAdapterListener(final LinearLayoutManager layoutManager) {


        mAdapter.setOnCheckedCountChangeListener(new OnCheckedCountChangeListener() {
            @Override
            public void onCheckedCountChanged(int count) {
                mTvSelectedCount.setText(String.format(Locale.CHINA, "已选择 %d 项", count));
            }
        });
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
        mAdapter.setOnRvItemLongClickListener(new BaseSimpleRvAdapter.OnRvItemLongClickListener() {
            @Override
            public boolean onLongClick(int position) {
                mAdapter.getDataList().get(position).setSelected(true);
                showEditUi(true);
                return true;
            }
        });
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void onStart() {
        //如有黑名单，则显示黑名单列表，没有就显示黑名单为空
        mTotalNum = mBlackNumberDao.getTotalNumber();
        if (mTotalNum > 0) {
            mBlackContactInfoList = mBlackNumberDao.getPagesBlackNumber(0, 150);
            if (mRvBlack.getVisibility() != View.VISIBLE) {
                showBlackNumList(true);
            }
            if (mAdapter == null) {
                initRvAndAdapter();
            } else {
                mAdapter.setDataList(mBlackContactInfoList);
                mAdapter.notifyDataSetChanged();
            }
            showEditUi(false);
        } else {
            setTitle("黑名单列表");
            showBlackNumList(false);
            mTvCancel.setVisibility(View.GONE);
            mTvSelectedCount.setVisibility(View.GONE);
            mTvSelectAll.setVisibility(View.GONE);
        }
        super.onStart();
    }

    @OnClick({R.id.iv_edit, R.id.iv_add, R.id.iv_delete, R.id.tv_cancel, R.id.tv_select_all})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_edit:
                if (mTotalNum <= 0) {
                    showToast("当前没有任何名单可以编辑");
                    return;
                }
                showEditUi(true);
                break;
            case R.id.iv_add:
                showDialog();
                break;
            case R.id.iv_delete:
                deleteItems();
                break;
            case R.id.tv_cancel:
                showEditUi(false);
                break;
            case R.id.tv_select_all:
                selectAllItem();
                break;
        }
    }

    private void selectAllItem() {
        for (BlackContactInfo blackContactInfo : mAdapter.getDataList()) {
            blackContactInfo.setSelected(mIsSelectAll);
        }
        mAdapter.notifyDataSetChanged();
        mTvSelectAll.setText(mIsSelectAll ? "全不选" : "全选");
        mIsSelectAll = !mIsSelectAll;
    }

    private void deleteItems() {
        List<BlackContactInfo> contactInfoList = new ArrayList<>();
        for (BlackContactInfo blackContactInfo : mAdapter.getDataList()) {
            if (blackContactInfo.isSelected()) {
                mBlackNumberDao.delete(blackContactInfo);
                contactInfoList.add(blackContactInfo);
            }
        }
        mAdapter.getDataList().removeAll(contactInfoList);
        mAdapter.notifyDataSetChanged();
        showEditUi(false);
        //没有可编辑的黑名单
        mTotalNum = mBlackNumberDao.getTotalNumber();
        if (mTotalNum == 0) {
            showBlackNumList(false);
        }
    }

    private void showEditUi(boolean show) {
        mAdapter.setShowCheckBox(show);
        mAdapter.notifyDataSetChanged();
        getSupportActionBar().setDisplayShowHomeEnabled(!show);
        getSupportActionBar().setDisplayHomeAsUpEnabled(!show);
        if (show) {
            setTitle("");
            int count = 0;
            for (BlackContactInfo blackContactInfo : mBlackContactInfoList) {
                if (blackContactInfo.isSelected()) {
                    count++;
                }
            }
            mTvSelectedCount.setText(String.format(Locale.CHINA, "已选择 %d 项", count));

            mTvCancel.setVisibility(View.VISIBLE);
            mTvSelectAll.setVisibility(View.VISIBLE);
            mTvSelectedCount.setVisibility(View.VISIBLE);

            mLlDelete.setVisibility(View.VISIBLE);
            mLlAdd.setVisibility(View.GONE);
            mLlEdit.setVisibility(View.GONE);
        } else {
            setTitle("黑名单列表");
            mTvCancel.setVisibility(View.GONE);
            mTvSelectedCount.setVisibility(View.GONE);
            mTvSelectAll.setVisibility(View.GONE);

            mLlDelete.setVisibility(View.GONE);
            mLlAdd.setVisibility(View.VISIBLE);
            mLlEdit.setVisibility(View.VISIBLE);
        }
    }

    private void showDialog() {
        new AlertDialog.Builder(this)
                .setItems(new String[]{"手动输入", "从联系人中选择"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                startActivity(EnterBlackNumberActivity.class);
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
