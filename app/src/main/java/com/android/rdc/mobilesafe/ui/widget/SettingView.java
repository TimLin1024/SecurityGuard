package com.android.rdc.mobilesafe.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.rdc.mobilesafe.R;

public class SettingView extends RelativeLayout {

    private String mTitle = "";
    private String mStatusOnStr = "";
    private String mStatusOffStr = "";
    private TextView mTvSettingStatus;
    private TextView mTvSettingTitle;
    private ToggleButton mToggleButton;
    private boolean mIsChecked;
    private OnCheckStatusChanged mOnCheckStatusChanged;

    public SettingView(Context context) {
        super(context);
        init(context);
    }

    public SettingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public SettingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingView);
        mTitle = typedArray.getString(R.styleable.SettingView_title);
        mStatusOnStr = typedArray.getString(R.styleable.SettingView_status_on);
        mStatusOffStr = typedArray.getString(R.styleable.SettingView_status_off);
        mIsChecked = typedArray.getBoolean(R.styleable.SettingView_status_ischecked, false);
        typedArray.recycle();
        init(context);
        updateStateText();
    }


    private void init(Context context) {
        View view = View.inflate(context,R.layout.setting_view,null);
        this.addView(view);
        mTvSettingStatus = $(R.id.tv_setting_status);
        mTvSettingTitle = $(R.id.tv_setting_title);
        mToggleButton = $(R.id.toggle_btn_setting_status);
        mTvSettingTitle.setText(mTitle);
        mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setChecked(isChecked);
                // TODO: 2017/7/4 0004   注意此处是 SettingView.this 而非 buttonView
                mOnCheckStatusChanged.onCheckStatusChange(SettingView.this, isChecked);
            }
        });
    }

    public boolean isChecked() {
        return mToggleButton.isChecked();
    }

    public void setChecked(boolean checked) {
        mToggleButton.setChecked(checked);
        mIsChecked = checked;
        updateStateText();
    }

    private void updateStateText() {
        if (mIsChecked) {
            mTvSettingStatus.setText(mStatusOnStr);
        } else {
            mTvSettingStatus.setText(mStatusOffStr);
        }
    }

    public void setOnCheckStatusChanged(OnCheckStatusChanged onCheckStatusChanged) {
        mOnCheckStatusChanged = onCheckStatusChanged;
    }

    public interface OnCheckStatusChanged {
        void onCheckStatusChange(View v, boolean isChecked);
    }

    @SuppressWarnings("unchecked")
    private <T extends View> T $(int id) {
        return (T) findViewById(id);
    }

}
