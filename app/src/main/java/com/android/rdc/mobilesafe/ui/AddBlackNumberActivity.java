package com.android.rdc.mobilesafe.ui;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseToolBarActivity;
import com.android.rdc.mobilesafe.dao.BlackNumberDao;
import com.android.rdc.mobilesafe.entity.BlackContactInfo;

import butterknife.BindView;
import butterknife.OnClick;

public class AddBlackNumberActivity extends BaseToolBarActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.et_black_number)
    EditText mEtBlackNumber;
    @BindView(R.id.et_black_name)
    EditText mEtBlackName;
    @BindView(R.id.cb_black_number_tel)
    CheckBox mCbBlackNumberTel;
    @BindView(R.id.cb_black_number_sms)
    CheckBox mCbBlackNumberSms;
    @BindView(R.id.tv_confirm)
    TextView mTvConfirm;

    private BlackNumberDao mBlackNumberDao;

    @Override
    protected int setResId() {
        return R.layout.activity_add_black_number;
    }

    @Override
    protected void initData() {
        mBlackNumberDao = new BlackNumberDao(getApplicationContext());
        mTvConfirm.setEnabled(false);
    }

    @Override
    protected void initView() {
        setTitle("黑名单号码");
        mCbBlackNumberTel.setOnCheckedChangeListener(this);
        mCbBlackNumberSms.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initListener() {
        mEtBlackNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mEtBlackNumber.getText() != null) {
                    if (mCbBlackNumberSms.isChecked() || mCbBlackNumberTel.isChecked()) {
                        mTvConfirm.setEnabled(true);
                    }
                }
            }
        });
    }


    @OnClick(R.id.tv_confirm)
    public void onViewClicked() {
        BlackContactInfo contactInfo = new BlackContactInfo();
        contactInfo.setContractName(mEtBlackName.getText().toString());
        contactInfo.setPhoneNumber(mEtBlackNumber.getText().toString());
        if (mCbBlackNumberSms.isChecked()) {
            if (mCbBlackNumberTel.isChecked()) {
                contactInfo.setMode(3);
            } else {
                contactInfo.setMode(2);
            }
        } else {
            if (mCbBlackNumberTel.isChecked()) {
                contactInfo.setMode(1);
            }
        }
        if (mBlackNumberDao.add(contactInfo)) {
            finish();
        } else {
            showToast("添加失败");
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (mEtBlackNumber.getText() != null) {
            if (isChecked) {
                mTvConfirm.setEnabled(true);
            } else {
                mTvConfirm.setEnabled(false);
            }
        } else {
            mTvConfirm.setEnabled(false);
        }
    }

}
