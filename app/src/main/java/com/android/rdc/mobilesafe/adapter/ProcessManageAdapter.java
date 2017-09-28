package com.android.rdc.mobilesafe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.entity.TaskInfo;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProcessManageAdapter extends RecyclerView.Adapter {

    private List<TaskInfo> mSystemTaskList;
    private List<TaskInfo> mUserTaskList;
    private Context mContext;

    public ProcessManageAdapter(List<TaskInfo> systemTaskList, List<TaskInfo> userTaskList, Context context) {
        mSystemTaskList = systemTaskList;
        mUserTaskList = userTaskList;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(viewType, parent, false);

        if (viewType == R.layout.item_indicator) {
            return new IndicatorVH(v);
        }
        return new ProcessManagerVH(v);


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            ((IndicatorVH) holder).bindView(String.format(Locale.CHINA, "运行中的用户应用进程 %d 个", mUserTaskList.size()));
        } else if (position == mUserTaskList.size() + 1) {
            ((IndicatorVH) holder).bindView(String.format(Locale.CHINA, "运行中的系统应用进程 %d 个", mSystemTaskList.size()));
        } else if (position <= mUserTaskList.size()) {
            ((ProcessManagerVH) holder).bindView(mUserTaskList.get(position - 1));
        } else {
            ((ProcessManagerVH) holder).bindView(mSystemTaskList.get(position - mUserTaskList.size() - 2));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == mUserTaskList.size() + 1) {
            return R.layout.item_indicator;
        } else {
            return R.layout.item_process_manager;
        }
    }

    @Override
    public int getItemCount() {
        return mSystemTaskList.size() + mUserTaskList.size() + 2;
    }

    class IndicatorVH extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_indicator)
        TextView mTvIndicator;

        IndicatorVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindView(String str) {
            mTvIndicator.setText(str);
        }
    }

    class ProcessManagerVH extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        @BindView(R.id.iv_app_icon)
        ImageView mIvAppIcon;
        @BindView(R.id.tv_process_name)
        TextView mTvAppName;
        @BindView(R.id.tv_app_size)
        TextView mTvAppSize;
        @BindView(R.id.cb_selected)
        CheckBox mCbSelected;

        private TaskInfo mTaskInfo;

        ProcessManagerVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindView(TaskInfo taskInfo) {
            mTaskInfo = taskInfo;

            mIvAppIcon.setImageDrawable(taskInfo.getIcon());
            mTvAppName.setText(taskInfo.getAppName());
            mTvAppSize.setText(Formatter.formatFileSize(mContext, taskInfo.getMemSize()));
            mCbSelected.setChecked(taskInfo.isChecked());

            mCbSelected.setOnCheckedChangeListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mTaskInfo.setChecked(isChecked);
        }
    }
}
