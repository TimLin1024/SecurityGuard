package com.android.rdc.mobilesafe.ui;

import android.content.SharedPreferences;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.android.rdc.mobilesafe.HomeActivity;
import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseScrollTbActivity;
import com.android.rdc.mobilesafe.constant.Constant;

public class OperatorSettingActivity extends BaseScrollTbActivity {

//    @BindView(R.id.spinner_operator_select)
//    Spinner mSpinnerOperatorSelect;
//    @BindView(R.id.btn_finish)
//    Button mBtnFinish;

    private static final String[] OPERATORS = {"中国联通", "中国电信", "中国移动"};
    private SharedPreferences mSp;


    @Override
    protected int setResId() {
        return R.layout.activity_operator_setting;
    }

    @Override
    protected void initData() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, OPERATORS);
//        mSpinnerOperatorSelect.setAdapter(arrayAdapter);
        mSp = getSharedPreferences(Constant.SP_CONFIG, MODE_PRIVATE);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mSp.edit()
                        .putBoolean(Constant.HAS_SET_OPERATOR, false)
                        .apply();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    @OnClick(R.id.btn_finish)
    public void onViewClicked() {
        mSp.edit()
                .putBoolean(Constant.HAS_SET_OPERATOR, true)
                .apply();
        startActivity(TrafficMonitoringActivity.class);
    }

    @Override
    public void showPre() {
        startActivityAndFinishSelf(HomeActivity.class);
    }

    @Override
    public void showNext() {
        startActivityAndFinishSelf(EnterPwdActivity.class);
    }
}
