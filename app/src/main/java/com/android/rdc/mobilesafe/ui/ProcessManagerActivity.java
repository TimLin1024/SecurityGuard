package com.android.rdc.mobilesafe.ui;

import android.app.ActivityManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.adapter.ProcessManageAdapter;
import com.android.rdc.mobilesafe.base.BaseToolBarActivity;
import com.android.rdc.mobilesafe.bean.TaskInfo;
import com.android.rdc.mobilesafe.util.ProgressDialogUtil;
import com.android.rdc.mobilesafe.util.SystemUtil;
import com.android.rdc.mobilesafe.util.TaskInfoParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;


public class ProcessManagerActivity extends BaseToolBarActivity {


    @BindView(R.id.tv_running_process_num)
    TextView mTvRunningProcessNum;
    @BindView(R.id.rv_running_process)
    RecyclerView mRvRunningProcess;
    @BindView(R.id.btn_clear_process)
    Button mBtnClearProcess;
    @BindView(R.id.btn_select_all)
    Button mBtnSelectAll;
    @BindView(R.id.btn_reverse)
    Button mBtnReverse;
    @BindView(R.id.tv_running_process_mem)
    TextView mTvRunningProcessMem;
    @BindView(R.id.iv_setting)
    ImageView mIvSetting;

    private List<TaskInfo> mUserTaskList;
    private List<TaskInfo> mSystemTaskList;
    private List<TaskInfo> mRunningTaskInfoList;
    private ProcessManageAdapter mAdapter;
    private int mRunningProcessCount;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        return super.onCreateOptionsMenu(menu);
    }

    //首先根据设置获取所有正在运行的进程
    //然后对进程进行操作，比如说清理掉，应该要有一个黑白名单？
    //数据项需要支持正选与反选，不能清理自身
    @Override
    protected int setResId() {
        return R.layout.activity_process_manager;
    }

    @Override
    protected void initData() {
        mUserTaskList = new ArrayList<>(32);
        mSystemTaskList = new ArrayList<>(32);
        ProgressDialogUtil.showDefaultDialog(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mRunningTaskInfoList = TaskInfoParser.parseTaskInfo(getApplicationContext());
                for (TaskInfo taskInfo : mRunningTaskInfoList) {
                    if (taskInfo.isUserApp()) {
                        mUserTaskList.add(taskInfo);
                    } else {
                        mSystemTaskList.add(taskInfo);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //更新显示内容
                        mRunningProcessCount = mRunningTaskInfoList.size();
                        mTvRunningProcessMem.setText(
                                String.format("可用/总内存：%s/%s",
                                        Formatter.formatFileSize(ProcessManagerActivity.this, SystemUtil.getAvailableMem(ProcessManagerActivity.this)),
                                        Formatter.formatFileSize(ProcessManagerActivity.this, SystemUtil.getTotalMem()))
                        );

                        mTvRunningProcessNum.setText(String.format(Locale.CHINA, "运行中的进程：%d 个", mRunningProcessCount));
                        mAdapter = new ProcessManageAdapter(mSystemTaskList, mUserTaskList, ProcessManagerActivity.this/*getApplicationContext()*/);
                        mRvRunningProcess.setLayoutManager(new LinearLayoutManager(ProcessManagerActivity.this));
                        mRvRunningProcess.addItemDecoration(new DividerItemDecoration(ProcessManagerActivity.this, DividerItemDecoration.VERTICAL));
                        mRvRunningProcess.setAdapter(mAdapter);
                        ProgressDialogUtil.dismiss();
                    }
                });
            }
        }).start();


    }

    @Override
    protected void initView() {
        setTitle("优化加速");
    }

    @Override
    protected void initListener() {

    }

    private void cleanProcess() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        long saveMem = 0;
        int count = 0;
        List<TaskInfo> killProcessList = new ArrayList<>();
        //注意不能直接 foreach 循环中 remove
        for (TaskInfo taskInfo : mUserTaskList) {
            if (taskInfo.isChecked()) {
                count++;
                saveMem += taskInfo.getMemSize();
                am.killBackgroundProcesses(taskInfo.getPackageName());
                killProcessList.add(taskInfo);//添加到删除列表中
            }
        }
        for (TaskInfo taskInfo : mSystemTaskList) {
            if (taskInfo.isChecked()) {
                count++;
                saveMem += taskInfo.getMemSize();
                am.killBackgroundProcesses(taskInfo.getPackageName());//移除

                killProcessList.add(taskInfo);//添加到列表中
            }
        }

        for (TaskInfo taskInfo : killProcessList) {
            if (taskInfo.isUserApp()) {
                mUserTaskList.remove(taskInfo);
            } else {
                mSystemTaskList.remove(taskInfo);
            }
        }
        mRunningProcessCount -= count;
        mTvRunningProcessMem.setText(
                String.format("可用/总内存：%s/%s",
                        Formatter.formatFileSize(this, SystemUtil.getAvailableMem(this)),
                        Formatter.formatFileSize(this, SystemUtil.getTotalMem()))
        );
        mTvRunningProcessNum.setText(String.format(Locale.CHINA, "运行中的进程：%d", mRunningProcessCount));
        showToast("清理了 " + count + " 个进程," + "回收了 " + Formatter.formatFileSize(this, saveMem) + "内存");

        mAdapter.notifyDataSetChanged();
    }

    private void selectAll() {
        for (TaskInfo taskInfo : mRunningTaskInfoList) {
            if (taskInfo.getPackageName().equals(getPackageName())) {//不能勾选自己
                continue;
            }
            taskInfo.setChecked(true);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void reverse() {
        for (TaskInfo taskInfo : mUserTaskList) {
            if (taskInfo.getPackageName().equals(getPackageName())) {//不能勾选自己
                continue;
            }
            taskInfo.setChecked(!taskInfo.isChecked());//设置为反选
        }
        for (TaskInfo taskInfo : mSystemTaskList) {
            taskInfo.setChecked(!taskInfo.isChecked());
        }
        mAdapter.notifyDataSetChanged();
    }


    @OnClick({R.id.btn_clear_process, R.id.btn_select_all, R.id.btn_reverse, R.id.iv_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_clear_process:
                cleanProcess();
                break;
            case R.id.btn_select_all:
                selectAll();
                break;
            case R.id.btn_reverse:
                reverse();
                break;
            case R.id.iv_setting:
                startActivity(ProcessCleanSettingActivity.class);
                break;
        }
    }

}
