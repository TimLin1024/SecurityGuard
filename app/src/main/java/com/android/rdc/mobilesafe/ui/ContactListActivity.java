package com.android.rdc.mobilesafe.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import butterknife.OnClick;

public class ContactListActivity extends BaseToolBarActivity {
    private static final String TAG = "ContactListActivity";
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 101;

    @BindView(R.id.rv)
    RecyclerView mRv;

    @BindView(R.id.tv_dialog)
    TextView mTvDialog;
    @BindView(R.id.index_bar)
    IndexBar mIndexBar;

    @BindView(R.id.btn_select_all)
    Button mBtnSelectAll;
    @BindView(R.id.tv_select_hint)
    TextView mTvSelectHint;
    @BindView(R.id.btn_confirm)
    Button mBtnConfirm;
    @BindView(R.id.ll)
    LinearLayout mLl;

//    @BindView(R.id.wave_side_bar_view)
//    WaveSideBarView mWaveSideBarView;


    private ContactAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private CountDownLatch mCountDownLatch;

    private List<ContactInfo> mContactInfoList;


    @Override
    protected int setResId() {
        return R.layout.activity_contact_list;
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initView() {

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

        mContactInfoList = ContactInfoParser.getSystemContact(this);
        mAdapter = new ContactAdapter();
        Collections.sort(mContactInfoList);
        mAdapter.setDataList(mContactInfoList);
        mRv.setAdapter(mAdapter);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRv.setLayoutManager(mLinearLayoutManager);
        mRv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRv.addItemDecoration(new TitleItemDecoration(this, mContactInfoList));

        mIndexBar.setPressedBg(getResources().getColor(R.color.graye5));
        mIndexBar.setLayoutManager(mLinearLayoutManager);
        mIndexBar.setHintTextView(mTvDialog);
        mIndexBar.setSourceData(mContactInfoList);

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


    @OnClick({R.id.btn_confirm, R.id.btn_select_all})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                showSharedDialog();
                break;
            case R.id.btn_select_all:
                for (ContactInfo contactInfo : mContactInfoList) {
                    contactInfo.setChecked(true);
                }
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void showSharedDialog() {
        BottomSheetDialog bottomSheetDialog = null;
        if (bottomSheetDialog == null) {
            bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setCancelable(true);
            bottomSheetDialog.setCanceledOnTouchOutside(true);
            //这里的layout是要显示的布局内容，里面可以放RecyclerView等
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_bottom_sheet, null);
            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.show();

            Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
            Button btnSure = (Button) view.findViewById(R.id.btn_sure);
            final BottomSheetDialog finalBottomSheetDialog1 = bottomSheetDialog;
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalBottomSheetDialog1.dismiss();
                }
            });
            btnSure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 2017/10/6 0006 获取选中的 item 列表

                    finalBottomSheetDialog1.dismiss();
                }
            });


            //以下设置是为了解决：下滑隐藏dialog后，再次调用show方法显示时，不能弹出Dialog----在真机测试时不写下面的方法也未发现问题
            View delegateView = bottomSheetDialog.getDelegate().findViewById(android.support.design.R.id.design_bottom_sheet);
            final BottomSheetBehavior<View> sheetBehavior = BottomSheetBehavior.from(delegateView);
            final BottomSheetDialog finalBottomSheetDialog = bottomSheetDialog;
            sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                //在下滑隐藏结束时才会触发
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        finalBottomSheetDialog.dismiss();
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }

                //每次滑动都会触发
                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    System.out.println("onSlide = [" + bottomSheet + "], slideOffset = [" + slideOffset + "]");
                }
            });
        } else {
            bottomSheetDialog.show();
        }
    }

}
