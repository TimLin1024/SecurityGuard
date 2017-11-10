package com.android.rdc.mobilesafe.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.adapter.ContactAdapter;
import com.android.rdc.mobilesafe.base.BaseToolBarActivity;
import com.android.rdc.mobilesafe.bean.BlackContactInfo;
import com.android.rdc.mobilesafe.bean.ContactInfo;
import com.android.rdc.mobilesafe.callback.OnCheckedCountChangeListener;
import com.android.rdc.mobilesafe.dao.BlackNumberDao;
import com.android.rdc.mobilesafe.ui.decoration.TitleItemDecoration;
import com.android.rdc.mobilesafe.ui.widget.IndexBar;
import com.android.rdc.mobilesafe.util.ContactInfoParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

public class ContactListActivity extends BaseToolBarActivity {
    private static final String TAG = "ContactListActivity";
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 101;
    @BindView(R.id.index_bar)
    IndexBar mIndexBar;
    @BindView(R.id.tv_dialog)
    TextView mTvDialog;
    @BindView(R.id.rv)
    RecyclerView mRv;
    @BindView(R.id.tv_selected_count)
    TextView mTvSelectedCount;
    @BindView(R.id.tv_select_all)
    TextView mTvSelectAll;
    @BindView(R.id.tv_confirm)
    TextView mTvConfirm;
    @BindView(R.id.ll_sure)
    LinearLayout mLlSure;

    private ContactAdapter mAdapter;
    private List<ContactInfo> mContactInfoList;
    private BottomSheetDialog mBottomSheetDialog;
    private boolean mIsSelectAll = true;//默认显示全选

    @Override
    protected int setResId() {
        return R.layout.activity_contact_list;
    }

    @Override
    protected void initData() {
        //6.0 以上动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            //6.0 以下或者是6.0+但是已经批准权限就直接更新 UI
            initUi();
        }
    }

    @Override
    protected void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    private void initUi() {
        mContactInfoList = ContactInfoParser.readContacts(getApplicationContext());//获取联系人（换一种方式）
        mAdapter = new ContactAdapter();
        Collections.sort(mContactInfoList);//排序
        mAdapter.setDataList(mContactInfoList);
        mRv.setAdapter(mAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRv.setLayoutManager(linearLayoutManager);
        mRv.setHasFixedSize(true);
        mRv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRv.addItemDecoration(new TitleItemDecoration(this, mContactInfoList));

        mIndexBar.setLayoutManager(linearLayoutManager);
        mIndexBar.setHintTextView(mTvDialog);
        mIndexBar.setSourceData(mContactInfoList);

        mTvSelectedCount.setText("已选择 0 项");
    }

    @Override
    protected void initListener() {
        mAdapter.setCheckedCountChangeListener(new OnCheckedCountChangeListener() {
            @Override
            public void onCheckedCountChanged(int count) {
                mTvSelectedCount.setText(String.format(Locale.CHINA, "已选择 %d 项", count));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
//                mCountDownLatch.countDown();
                initUi();
            } else {
                Toast.makeText(this, "需要查看联系人权限，才能在联系人中选取", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void showSharedDialog() {
        if (mBottomSheetDialog == null) {
            mBottomSheetDialog = new BottomSheetDialog(this);
            mBottomSheetDialog.setCancelable(true);
            mBottomSheetDialog.setCanceledOnTouchOutside(true);
            //这里的layout是要显示的布局内容，里面可以放RecyclerView等
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_bottom_sheet, null);
            mBottomSheetDialog.setContentView(view);

            TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
            TextView tvSure = (TextView) view.findViewById(R.id.tv_sure);
            final CheckBox cbInterceptPhone = (CheckBox) view.findViewById(R.id.cb_intercept_phone);
            final CheckBox cbInterceptSms = (CheckBox) view.findViewById(R.id.cb_intercept_sms);

            final BottomSheetDialog finalBottomSheetDialog1 = mBottomSheetDialog;
            tvCancel.setOnClickListener(v -> finalBottomSheetDialog1.dismiss());
            tvSure.setOnClickListener(v -> insertIntoDb(cbInterceptPhone, cbInterceptSms, finalBottomSheetDialog1));
        }
        mBottomSheetDialog.show();
    }

    private void insertIntoDb(CheckBox cbInterceptPhone, CheckBox cbInterceptSms, BottomSheetDialog finalBottomSheetDialog1) {
        int mode;
        if (cbInterceptPhone.isChecked()) {
            if (cbInterceptSms.isChecked()) {
                mode = 3;
            } else {
                mode = 1;
            }
        } else {
            if (cbInterceptSms.isChecked()) {
                mode = 2;
            } else {
                finalBottomSheetDialog1.dismiss();
                return;
            }
        }

        BlackNumberDao blackNumberDao = new BlackNumberDao(ContactListActivity.this);
        int sum = 0;
        List<ContactInfo> contactInfoListToAdd = new ArrayList<>();
        for (ContactInfo contactInfo : mContactInfoList) {
            if (contactInfo.isChecked()) {
                if (blackNumberDao.isNumberExist(contactInfo.getPhoneNum())) {//检测，避免重复
                    continue;
                }
                sum++;
                contactInfoListToAdd.add(contactInfo);
            }
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMax(sum);
        progressDialog.show();
        int progress = 0;
        BlackContactInfo blackContactInfo = new BlackContactInfo();//创建一个即可，BlackNumberDao.add() 方法只是从中读取数据
        for (ContactInfo contactInfo : contactInfoListToAdd) {
            blackContactInfo.setContractName(contactInfo.getName());
            blackContactInfo.setMode(mode);
            blackContactInfo.setPhoneNumber(contactInfo.getPhoneNum());
            blackNumberDao.add(blackContactInfo);
            progress++;
            progressDialog.setProgress(progress);
        }
        progressDialog.dismiss();
        finalBottomSheetDialog1.dismiss();
        finish();
    }

    @OnClick({R.id.tv_cancel, R.id.tv_select_all, R.id.ll_sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_select_all:
                for (ContactInfo contactInfo : mContactInfoList) {
                    contactInfo.setChecked(mIsSelectAll);
                }
                mAdapter.notifyDataSetChanged();
                mTvSelectAll.setText(mIsSelectAll ? "全不选" : "全选");
                mIsSelectAll = !mIsSelectAll;
                break;
            case R.id.ll_sure:
                showSharedDialog();
                break;
        }
    }

}
