package com.android.rdc.mobilesafe.ui;

import android.content.SharedPreferences;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.rdc.mobilesafe.HomeActivity;
import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseScrollTbActivity;
import com.android.rdc.mobilesafe.constant.Constant;

import butterknife.BindView;
import butterknife.OnClick;

public class TrafficSettingActivity extends BaseScrollTbActivity {

    @BindView(R.id.ll_set_operator)
    LinearLayout mLlSetOperator;
    @BindView(R.id.tv_modify_command)
    TextView mTvModifyCommand;
    @BindView(R.id.tv_operator_info)
    TextView mTvOperatorInfo;
    private SharedPreferences mSp;

    @Override
    protected int setResId() {
        return R.layout.activity_traffic_setting;
    }

    @Override
    protected void initData() {
        mSp = getSharedPreferences(Constant.SP_NAME_CONFIG, MODE_PRIVATE);
    }

    @Override
    protected void initView() {
        setTitle("流量监控设置");
    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void onResume() {
        String str = /*mSp.getString(Constant.KEY_CARD_CITY, "") + "-" +
                mSp.getString(Constant.KEY_CARD_OPERATOR, "") + "-" +*/
                mSp.getString(Constant.KEY_CARD_BRAND, "");
        mTvOperatorInfo.setText(str);
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void showPre() {
        startActivityAndFinishSelf(HomeActivity.class);
    }

    @Override
    public void showNext() {
        startActivityAndFinishSelf(EnterPwdActivity.class);
    }

    @OnClick({R.id.ll_set_operator, R.id.tv_modify_command})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_set_operator:
                startActivity(OperatorSettingActivity.class);
                break;
            case R.id.tv_modify_command:
                startActivity(ModifySmsCommandActivity.class);
                break;
        }
    }
}
