package com.android.rdc.mobilesafe.ui;

import android.content.SharedPreferences;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.rdc.mobilesafe.HomeActivity;
import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseScrollTbActivity;
import com.android.rdc.mobilesafe.bean.ProvinceCityBean;
import com.android.rdc.mobilesafe.constant.Constant;
import com.android.rdc.mobilesafe.util.IOUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class TrafficSettingActivity extends BaseScrollTbActivity {

    private static final String[] OPERATORS = {"中国联通", "中国电信", "中国移动"};
    @BindView(R.id.ll_set_operator)
    LinearLayout mLlSetOperator;
    @BindView(R.id.tv_modify_command)
    TextView mTvModifyCommand;
    private SharedPreferences mSp;
    private List<ProvinceCityBean> mProvinceCityBeanList;
    private List<List<ProvinceCityBean.CityBean>> mListList = new ArrayList<>(35);

    @Override
    protected int setResId() {
        return R.layout.activity_traffic_setting;
    }

    @Override
    protected void initData() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, OPERATORS);
        mSp = getSharedPreferences(Constant.SP_CONFIG, MODE_PRIVATE);

        InputStream inputStream = null;
        try {
            inputStream = getAssets().open("province.json");
            mProvinceCityBeanList = new Gson().fromJson(new InputStreamReader(inputStream), new TypeToken<List<ProvinceCityBean>>() {
            }.getType());//解析省市数据集
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(inputStream);
        }
        for (ProvinceCityBean provinceCityBean : mProvinceCityBeanList) {
            mListList.add(provinceCityBean.getCity());
        }
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

    @OnClick({R.id.ll_set_operator, R.id.tv_modify_command})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_set_operator:

//                final int[] op1 = new int[1];
//                OptionsPickerView optionsPickerView = new OptionsPickerView.Builder(this,
//                        (options1, options2, options3, v) -> op1[0] = options1).setLabels("1", "2", "3")
//                        .build();
//                optionsPickerView.setPicker(mProvinceCityBeanList, mListList);//第二项设置
//                optionsPickerView.show();
                startActivity(OperatorSettingActivity.class);
                break;
            case R.id.tv_modify_command:
                startActivity(ModifySmsCommandActivity.class);
                break;
        }
    }
}
