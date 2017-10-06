package com.android.rdc.mobilesafe.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.adapter.ContactAdapter;
import com.android.rdc.mobilesafe.base.BaseToolBarActivity;
import com.android.rdc.mobilesafe.entity.ContactInfo;
import com.android.rdc.mobilesafe.ui.decoration.TitleItemDecoration;
import com.android.rdc.mobilesafe.ui.widget.IndexBar;
import com.android.rdc.mobilesafe.util.ContactInfoParser;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import butterknife.BindView;

public class ContactListActivity extends BaseToolBarActivity {
    private static final String TAG = "ContactListActivity";
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 101;

    @BindView(R.id.rv)
    RecyclerView mRv;

    AppBarLayout mAppBarLayout;
    @BindView(R.id.tv_dialog)
    TextView mTvDialog;
    @BindView(R.id.index_bar)
    IndexBar mIndexBar;

//    @BindView(R.id.wave_side_bar_view)
//    WaveSideBarView mWaveSideBarView;

    private int mSuspensionHeight;
    private int mCurrentPosition;

    private ContactAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    CountDownLatch mCountDownLatch;

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

        //6.0 以上动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
            mCountDownLatch = new CountDownLatch(1);
            try {
                mCountDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        final List<ContactInfo> contactInfoList = ContactInfoParser.getSystemContact(this);

        Collections.sort(contactInfoList);
        mAdapter.setDataList(contactInfoList);
        mRv.setAdapter(mAdapter);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRv.setLayoutManager(mLinearLayoutManager);
        mRv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRv.addItemDecoration(new TitleItemDecoration(this, contactInfoList));

        mIndexBar.setPressedBg(getResources().getColor(R.color.graye5));
        mIndexBar.setLayoutManager(mLinearLayoutManager);
        mIndexBar.setHintTextView(mTvDialog);
        mIndexBar.setSourceData(contactInfoList);

//        mSideBar.setTextView(mTvDialog);
//        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
//            @Override
//            public void onTouchingLetterChanged(String s) {
////                mTvDialog.setText(s);
////                mTvDialog.setVisibility(View.VISIBLE);
////                mTvDialog.postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        mTvDialog.setVisibility(View.GONE);
////                    }
////                }, 5000);
//                int index = 0;
//                for (ContactInfo contactInfo : contactInfoList) {
//                    index++;
//                    if (contactInfo.getFirstLetter() != null && contactInfo.getFirstLetter().startsWith(s)) {
//                        mLinearLayoutManager.scrollToPositionWithOffset(index, 0);
//                        break;
//                    }
//                }
//            }
//        });

    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                mCountDownLatch.countDown();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


}
