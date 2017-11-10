package com.android.rdc.mobilesafe.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.util.DensityUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomDialog extends Dialog {

    @BindView(R.id.btn_cancel)
    Button mBtnCancel;
    @BindView(R.id.btn_sure)
    Button mBtnSure;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_msg)
    TextView mTvMsg;

    private ButtonCallBack mCallBack;

    public CustomDialog(@NonNull Context context) {
        super(context);
    }

    public CustomDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context /*R.style.dialog_custom*/);//引入自定义对话框形式
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_custom);
        ButterKnife.bind(this);
        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = DensityUtil.density2px(getContext(), 350);
        lp.height = DensityUtil.density2px(getContext(), 180);
//        lp.horizontalMargin = 10;
        lp.gravity = Gravity.BOTTOM;
        win.setAttributes(lp);
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

    public void setDialogTitle(String title) {
        mTvTitle.setText(title);
    }

    public void setDialogMsg(String msg) {
        mTvMsg.setText(msg);
    }

    public void setCallBack(ButtonCallBack callBack) {
        mCallBack = callBack;
    }

    public interface ButtonCallBack {
        void onSureClick();

        void onCancelClick();
    }
}
