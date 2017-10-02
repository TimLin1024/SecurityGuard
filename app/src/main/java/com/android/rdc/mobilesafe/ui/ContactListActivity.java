package com.android.rdc.mobilesafe.ui;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.adapter.ContactAdapter;
import com.android.rdc.mobilesafe.base.BaseToolBarActivity;
import com.android.rdc.mobilesafe.entity.ContactInfo;
import com.android.rdc.mobilesafe.ui.widget.SideBar;
import com.android.rdc.mobilesafe.util.ContactInfoParser;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactListActivity extends BaseToolBarActivity {
    private static final String TAG = "ContactListActivity";

    @BindView(R.id.rv)
    RecyclerView mRv;
    @BindView(R.id.tv_suspension)
    TextView mTvSuspension;
    @BindView(R.id.side_bar)
    SideBar mSideBar;

    AppBarLayout mAppBarLayout;
    @BindView(R.id.tv_dialog)
    TextView mTvDialog;

//    @BindView(R.id.wave_side_bar_view)
//    WaveSideBarView mWaveSideBarView;

    private int mSuspensionHeight;
    private int mCurrentPosition;

    private ContactAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private int mToolbarHeight;

    @Override
    protected int setResId() {
        return R.layout.activity_contact_list;
    }

    @Override
    protected void initData() {
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar);
    }

    @Override
    protected void initView() {
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ContactAdapter();
        final List<ContactInfo> contactInfoList = ContactInfoParser.getSystemContact(this);

        Collections.sort(contactInfoList);
        mAdapter.setDataList(contactInfoList);
        mRv.setAdapter(mAdapter);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRv.setLayoutManager(mLinearLayoutManager);
        mRv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                mTvDialog.setText(s);
                mTvDialog.setVisibility(View.VISIBLE);
                mTvDialog.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mTvDialog.setVisibility(View.GONE);
                    }
                }, 5000);
                int index = 0;
                for (ContactInfo contactInfo : contactInfoList) {
                    index++;
                    if (contactInfo.getFirstLetter() != null && contactInfo.getFirstLetter().startsWith(s)) {
                        mLinearLayoutManager.scrollToPosition(index);
                        break;
                    }
                }
            }
        });

    }

    @Override
    protected void initListener() {
        mRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mSuspensionHeight = mTvSuspension.getHeight();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                View view = mLinearLayoutManager.findViewByPosition(mCurrentPosition + 1);

                mToolbarHeight = mAppBarLayout.getHeight();
                if (view != null) {
                    if ((view.findViewById(R.id.tv_alphabet).getVisibility() == View.VISIBLE) && (view.getTop() + 30 <= mSuspensionHeight + mToolbarHeight)) {
                        mTvSuspension.setY(-(mSuspensionHeight - view.getTop()) + mToolbarHeight - 30);//加上 toolbar 高度
                    } else {
                        mTvSuspension.setY(mToolbarHeight);//加上 toolbar 高度
                    }
                }
                if (mCurrentPosition != mLinearLayoutManager.findFirstVisibleItemPosition()) {
                    mCurrentPosition = mLinearLayoutManager.findFirstVisibleItemPosition();//设置当前位置为第一个可见的位置
                    mTvSuspension.setY(mToolbarHeight);//设置 y 坐标为 0 //加上 toolbar 高度
                    updateSuspensionBar();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void updateSuspensionBar() {
        String letter = mAdapter.getDataList().get(mCurrentPosition).getFirstLetter();
        mTvSuspension.setText(letter == null ? "#" : String.valueOf(letter.charAt(0)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
