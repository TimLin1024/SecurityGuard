package com.android.rdc.mobilesafe.ui;

import android.content.SharedPreferences;
import android.widget.EditText;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseToolBarActivity;
import com.android.rdc.mobilesafe.constant.Constant;

import butterknife.BindView;
import butterknife.OnClick;

public class ModifySmsCommandActivity extends BaseToolBarActivity {

    @BindView(R.id.et_command)
    EditText mEtCommand;
    @BindView(R.id.et_receiver_num)
    EditText mEtReceiverNum;

    private SharedPreferences mSp;

    @Override
    protected int setResId() {
        return R.layout.activity_modify_sms_command;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        mSp = getSharedPreferences(Constant.SP_CONFIG, MODE_PRIVATE);
        mEtCommand.setText(mSp.getString(Constant.KEY_SMS_COMMAND, ""));
        mEtReceiverNum.setText(mSp.getString(Constant.KEY_COMMAND_RECEIVER, ""));
    }

    @Override
    protected void initListener() {

    }

    @OnClick(R.id.btn_modify_and_check)
    public void onViewClicked() {
        mSp.edit()
                .putString(Constant.KEY_COMMAND_RECEIVER, mEtReceiverNum.getText().toString())
                .putString(Constant.KEY_SMS_COMMAND, mEtCommand.getText().toString())
                .apply();
    }
}
