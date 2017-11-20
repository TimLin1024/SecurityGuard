package com.android.rdc.mobilesafe.ui;

import android.content.SharedPreferences;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.adapter.DialogRvAdapter;
import com.android.rdc.mobilesafe.base.BaseToolBarActivity;
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

public class OperatorSettingActivity extends BaseToolBarActivity {

    @BindView(R.id.tv_cancel)
    TextView mTvCancel;
    @BindView(R.id.tv_selected_count)
    TextView mTvSelectedCount;
    @BindView(R.id.tv_select_all)
    TextView mTvSelectAll;
    @BindView(R.id.tv_province)
    TextView mTvProvince;
    @BindView(R.id.tv_city)
    TextView mTvCity;
    @BindView(R.id.tv_operator)
    TextView mTvOperator;
    @BindView(R.id.tv_brand)
    TextView mTvBrand;

    private List<ProvinceCityBean> mProvinceCityBeanList;
    private BottomSheetDialog mBottomSheetDialog;
    private TextView mTvDialogTitle;
    private DialogRvAdapter mAdapter;
    private int mProvinceIndex = -1;
    private int mCityIndex;
    private int mOperatorNameIndex;
    private int mBrandIndex;
    private List<String> mOperatorNameList;
    private List<String> mBrandNameList;
    private SelectType mSelectType;
    private SharedPreferences mSp;

    private String mProvinceName;
    private String mCityName;
    private String mOperatorName;
    private String mBrandName;

    @Override
    protected int setResId() {
        return R.layout.activity_operator_setting;
    }

    @Override
    protected void initData() {
        mSp = getSharedPreferences(Constant.SP_NAME_CONFIG, MODE_PRIVATE);

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

        mOperatorNameList = new ArrayList<>();
        mOperatorNameList.add("中国联通");
        mOperatorNameList.add("中国移动");
        mOperatorNameList.add("中国电信");

        mBrandNameList = new ArrayList<>();
        mBrandNameList.add("联通 2G/3G");
        mBrandNameList.add("联通 4G");
        mBrandNameList.add("日租卡");
    }

    @Override
    protected void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        mTvSelectAll.setText("保存");
        mTvSelectedCount.setText("设置运营商");

        mTvProvince.setText(mSp.getString(Constant.KEY_CARD_PROVINCE, "请选择"));
        mTvCity.setText(mSp.getString(Constant.KEY_CARD_CITY, "请选择"));
        mTvOperator.setText(mSp.getString(Constant.KEY_CARD_OPERATOR, "请选择"));
        mTvBrand.setText(mSp.getString(Constant.KEY_CARD_BRAND, "请选择"));

        mProvinceIndex = mSp.getInt(Constant.KEY_CARD_PROVINCE_INDEX, 0);
        mCityIndex = mSp.getInt(Constant.KEY_CARD_CITY_INDEX, 0);
        mBrandIndex = mSp.getInt(Constant.KEY_CARD_BRAND_INDEX, 0);
        mOperatorNameIndex = mSp.getInt(Constant.KEY_CARD_OPERATOR_INDEX, 0);
    }

    @Override
    protected void initListener() {

    }

    @OnClick({R.id.tv_cancel, R.id.tv_select_all, R.id.ll_province, R.id.ll_city, R.id.ll_operator, R.id.ll_brand})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_select_all:
                saveData();
                finish();
                break;
            case R.id.ll_province:
                mSelectType = SelectType.PROVINCE;
                showBottomDialog();
                break;
            case R.id.ll_city:
                mSelectType = SelectType.CITY;
                showBottomDialog();
                break;
            case R.id.ll_operator:
                mSelectType = SelectType.OPERATOR_NAME;
                showBottomDialog();
                break;
            case R.id.ll_brand:
                mSelectType = SelectType.BRAND;
                showBottomDialog();
                break;
        }
    }

    private void saveData() {
        //确认运营商之后要设置校准指令
        mSp.edit()
                .putString(Constant.KEY_CARD_PROVINCE, mProvinceName)
                .putString(Constant.KEY_CARD_BRAND, mBrandName)
                .putString(Constant.KEY_CARD_OPERATOR, mOperatorName)
                .putString(Constant.KEY_CARD_CITY, mCityName)
                .putInt(Constant.KEY_CARD_PROVINCE_INDEX, mProvinceIndex)
                .putInt(Constant.KEY_CARD_BRAND_INDEX, mBrandIndex)
                .putInt(Constant.KEY_CARD_OPERATOR_INDEX, mOperatorNameIndex)
                .putInt(Constant.KEY_CARD_CITY_INDEX, mCityIndex)
                .putBoolean(Constant.HAS_SET_OPERATOR, mOperatorName != null)
                .apply();

        if (mOperatorName == null) {
            return;
        }
        switch (mOperatorName) {
            case "中国联通":
                mSp.edit()
                        .putString(Constant.KEY_COMMAND_RECEIVER, "10010")
                        .putString(Constant.KEY_SMS_COMMAND, "CXLL")
                        .apply();
                break;
            case "中国电信":
                mSp.edit()
                        .putString(Constant.KEY_COMMAND_RECEIVER, "10001")
                        .putString(Constant.KEY_SMS_COMMAND, "108")
                        .apply();
                break;
            case "中国移动":
                mSp.edit()
                        .putString(Constant.KEY_COMMAND_RECEIVER, "10086")
                        .putString(Constant.KEY_SMS_COMMAND, "cxgprs")
                        .apply();
                break;
            default:
                mSp.edit()
                        .putString(Constant.KEY_COMMAND_RECEIVER, "")
                        .putString(Constant.KEY_SMS_COMMAND, "")
                        .apply();
        }
    }

    private void showBottomDialog() {
        if (mBottomSheetDialog == null) {
            mBottomSheetDialog = new BottomSheetDialog(this);
            RelativeLayout rlRoot = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.dialog_list, null);
            mBottomSheetDialog.setContentView(rlRoot);
            mTvDialogTitle = (TextView) rlRoot.findViewById(R.id.tv_title);
            Button btn = (Button) rlRoot.findViewById(R.id.btn_cancel);
            btn.setOnClickListener(view -> mBottomSheetDialog.dismiss());
            RecyclerView rvListDialog = (RecyclerView) rlRoot.findViewById(R.id.rv);
            rvListDialog.setLayoutManager(new LinearLayoutManager(this));
            rvListDialog.setHasFixedSize(true);
            rvListDialog.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            mAdapter = new DialogRvAdapter();
            setRvClickListener();
            rvListDialog.setAdapter(mAdapter);
        }
        switch (mSelectType) {
            case PROVINCE:
                mTvDialogTitle.setText("省份");
                mAdapter.setDataList(mProvinceCityBeanList);
                if (mProvinceIndex >= 0) {
                    mAdapter.setPosition(mProvinceIndex);
                }
                break;
            case CITY:
                if ("请选择".contentEquals(mTvProvince.getText())) {
                    showToast("请先选择省份");
                    return;
                } else {
                    mTvDialogTitle.setText("城市");
                    mAdapter.setDataList(mProvinceCityBeanList.get(mProvinceIndex).getCity());
                    mAdapter.setPosition(mCityIndex);
                }
                break;
            case BRAND:
                mAdapter.setDataList(mBrandNameList);
                mAdapter.setPosition(mBrandIndex);
                break;
            case OPERATOR_NAME:
                mAdapter.setDataList(mOperatorNameList);
                mAdapter.setPosition(mOperatorNameIndex);
                break;
        }
        mAdapter.notifyDataSetChanged();
        mBottomSheetDialog.show();
    }

    private void setRvClickListener() {
        mAdapter.setOnRvItemClickListener(position -> {
            switch (mSelectType) {
                case PROVINCE:
                    mProvinceIndex = position;
                    mProvinceName = mProvinceCityBeanList.get(position).getName();
                    mTvProvince.setText(mProvinceName);
                    break;
                case CITY:
                    mCityIndex = position;
                    mCityName = mProvinceCityBeanList.get(mProvinceIndex).getCity().get(position).getName();
                    mTvCity.setText(mCityName);
                    break;
                case OPERATOR_NAME:
                    mOperatorNameIndex = position;
                    mOperatorName = mOperatorNameList.get(position);
                    mTvOperator.setText(mOperatorName);
                    break;
                case BRAND:
                    mBrandIndex = position;
                    mBrandName = mBrandNameList.get(position);
                    mTvBrand.setText(mBrandName);
                    break;
            }
            mBottomSheetDialog.dismiss();
        });
    }

    enum SelectType {
        PROVINCE, CITY, BRAND, OPERATOR_NAME
    }
}
