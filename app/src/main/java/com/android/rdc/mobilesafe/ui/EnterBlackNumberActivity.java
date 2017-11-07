package com.android.rdc.mobilesafe.ui;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseToolBarActivity;
import com.android.rdc.mobilesafe.dao.BlackNumberDao;
import com.android.rdc.mobilesafe.bean.BlackContactInfo;

import butterknife.BindView;
import butterknife.OnClick;

public class EnterBlackNumberActivity extends BaseToolBarActivity implements CompoundButton.OnCheckedChangeListener {


    @BindView(R.id.et_number)
    EditText mEtNumber;
    @BindView(R.id.cb_intercept_sms)
    CheckBox mCbInterceptSms;
    @BindView(R.id.cb_intercept_phone)
    CheckBox mCbInterceptPhone;
    @BindView(R.id.tv_sure)
    TextView mTvSure;

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
        mCbInterceptSms.setOnCheckedChangeListener(this);
        mCbInterceptPhone.setOnCheckedChangeListener(this);

        mEtNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateTvEnableState();
            }
        });
    }

    private void updateTvEnableState() {
        if (!TextUtils.isEmpty(mEtNumber.getText()) &&
                (mCbInterceptPhone.isChecked() || mCbInterceptSms.isChecked())) {
            mTvSure.setEnabled(true);
        } else {
            mTvSure.setEnabled(false);
        }
    }

    @OnClick({R.id.tv_cancel, R.id.tv_sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_sure:
                insertNumberToDb();
                finish();
                break;
        }
    }

    private void insertNumberToDb() {
        BlackNumberDao blackNumberDao = new BlackNumberDao(getApplicationContext());
        String number = mEtNumber.getText().toString();
        if (blackNumberDao.isNumberExist(number)) {
            return;
        }
        BlackContactInfo contactInfo = new BlackContactInfo();
        contactInfo.setPhoneNumber(number);
        int mode = 0;
        if (mCbInterceptSms.isChecked()) {
            if (mCbInterceptPhone.isChecked()) {
                mode = 3;
            } else {
                mode = 2;//只是拦截短信
            }
        } else if (mCbInterceptPhone.isChecked()) {
            mode = 1;
        }
        contactInfo.setMode(mode);

        blackNumberDao.add(contactInfo);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        updateTvEnableState();
    }
}
