package com.android.rdc.mobilesafe.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.android.rdc.mobilesafe.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomDialog extends Dialog {

    @BindView(R.id.cb_intercept_sms)
    CheckBox mCbInterceptSms;
    @BindView(R.id.cb_intercept_phone)
    CheckBox mCbInterceptPhone;
    @BindView(R.id.btn_cancel)
    Button mBtnCancel;
    @BindView(R.id.btn_sure)
    Button mBtnSure;

    private ButtonCallBack mCallBack;

    public CustomDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, R.style.dialog_custom);//引入自定义对话框形式
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_custom);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_cancel, R.id.btn_sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                if (mCallBack != null) {
                    mCallBack.onCancelClick();
                }
                break;
            case R.id.btn_sure:
                if (mCallBack != null) {
                    mCallBack.onSureClick();
                }
                break;
        }
    }

    public void setCallBack(ButtonCallBack callBack) {
        mCallBack = callBack;
    }

    public interface ButtonCallBack {
        void onSureClick();

        void onCancelClick();
    }
}
