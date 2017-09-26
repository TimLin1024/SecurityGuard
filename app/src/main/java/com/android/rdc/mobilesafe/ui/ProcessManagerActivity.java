package com.android.rdc.mobilesafe.ui;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseToolBarActivity;


public class ProcessManagerActivity extends BaseToolBarActivity {

    //首先根据设置获取所有正在运行的进程
    //然后对进程进行操作，比如说清理掉，应该要有一个黑白名单？
    //数据项需要支持正选与反选
    @Override
    protected int setResId() {
        return R.layout.activity_process_manager;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        setTitle("");
    }

    @Override
    protected void initListener() {

    }




}
