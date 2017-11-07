package com.android.rdc.mobilesafe.ui;

import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseSimpleRvAdapter;
import com.android.rdc.mobilesafe.base.BaseToolBarActivity;
import com.android.rdc.mobilesafe.bean.ProvinceCityBean;
import com.android.rdc.mobilesafe.util.IOUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    @BindView(R.id.ll_province)
    LinearLayout mLlProvince;
    @BindView(R.id.ll_city)
    LinearLayout mLlCity;
    @BindView(R.id.ll_operator)
    LinearLayout mLlOperator;
    @BindView(R.id.ll_brand)
    LinearLayout mLlBrand;
    private List<ProvinceCityBean> mProvinceCityBeanList;

    @Override
    protected int setResId() {
        return R.layout.activity_operator_setting;
    }

    @Override
    protected void initData() {
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
    }

    @Override
    protected void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        mTvSelectAll.setText("保存");
        mTvSelectedCount.setText("设置运营商");
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
                // TODO: 2017/11/5 0005 保存

                break;
            case R.id.ll_province:

                break;
            case R.id.ll_city:
                break;
            case R.id.ll_operator:
                break;
            case R.id.ll_brand:
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
                RecyclerView rv = (RecyclerView) LayoutInflater.from(this).inflate(R.layout.dialog_list, null);
                bottomSheetDialog.setContentView(rv);
                bottomSheetDialog.setTitle("品牌");
                rv.setLayoutManager(new LinearLayoutManager(this));
                rv.setHasFixedSize(true);


                BaseSimpleRvAdapter adapter = new BaseSimpleRvAdapter() {
                    @Override
                    protected int setLayoutId() {
                        return R.layout.item_indicator;
                    }

                    @Override
                    protected BaseRvHolder createConcreteViewHolder(View view) {
                        return new TextViewHolder(view);
                    }

                    class TextViewHolder extends BaseRvHolder {

//                        @BindView(R.id.tv_indicator)
                        TextView mTvIndicator;

                        public TextViewHolder(View itemView) {
                            super(itemView);
                            mTvIndicator = (TextView) itemView.findViewById(R.id.tv_indicator);
                        }

                        @Override
                        protected void bindView(Object o) {
                            mTvIndicator.setText("test");
                        }
                    }

                };
                //移动端，网络优化。
                adapter.setDataList(mProvinceCityBeanList);
                rv.setAdapter(adapter);
                bottomSheetDialog.show();
                break;
        }
    }
}
