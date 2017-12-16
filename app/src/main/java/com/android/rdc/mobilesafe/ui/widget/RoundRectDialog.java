package com.android.rdc.mobilesafe.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
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
import com.android.rdc.mobilesafe.util.DisplayUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RoundRectDialog extends Dialog {

    @BindView(R.id.btn_cancel)
    Button mBtnCancel;
    @BindView(R.id.btn_sure)
    Button mBtnSure;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_msg)
    TextView mTvMsg;

    private OnClickListener mNegativeButtonListener;
    private OnClickListener mPositiveButtonListener;


    RoundRectDialog(@NonNull Context context) {
        this(context, 0);
    }

    private RoundRectDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, R.style.RoundRectDialogStyle);//引入自定义对话框形式
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_custom);
        ButterKnife.bind(this);
        initSize();
    }

    private void initSize() {
        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        Point screenSizePoint = DisplayUtil.getScreenSize(getContext());
        lp.width = screenSizePoint.x - DensityUtil.density2px(getContext(), 20);//宽度为屏幕宽度 - 20
        lp.height = DensityUtil.density2px(getContext(), 180);//
        lp.gravity = Gravity.BOTTOM;//显示在底部
        win.setAttributes(lp);
    }


    @OnClick({R.id.btn_cancel, R.id.btn_sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                dismiss();//点击取消按钮默认隐藏窗口
                if (mNegativeButtonListener != null) {
                    mNegativeButtonListener.onClick(this, 0);
                }
                break;
            case R.id.btn_sure:
                if (mPositiveButtonListener != null) {
                    mPositiveButtonListener.onClick(this, 1);
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

    void setNegativeButtonListener(OnClickListener negativeButtonListener) {
        mNegativeButtonListener = negativeButtonListener;
    }

    void setPositiveButtonListener(OnClickListener positiveButtonListener) {
        mPositiveButtonListener = positiveButtonListener;
    }

    public final static class Builder {
        private RoundRectDialog mDialog;

        public Builder(Context context) {
            mDialog = new RoundRectDialog(context);
            mDialog.create();
        }

        public Builder setTitle(String title) {
            mDialog.mTvTitle.setText(title);
            return this;
        }

        public Builder setMsg(String msg) {
            mDialog.mTvMsg.setText(msg);
            return this;
        }

        public Builder setNegativeButton(CharSequence text, final OnClickListener listener) {
            mDialog.mBtnCancel.setText(text);
            mDialog.setNegativeButtonListener(listener);
            return this;
        }

        public Builder setPositiveButton(CharSequence text, final OnClickListener listener) {
            mDialog.mBtnSure.setText(text);
            mDialog.setPositiveButtonListener(listener);
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
            mDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            mDialog.setCancelable(cancelable);
            return this;
        }

        public RoundRectDialog show() {
            mDialog.show();
            return mDialog;
        }
    }
}
