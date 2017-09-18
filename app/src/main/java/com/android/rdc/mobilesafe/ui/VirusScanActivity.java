package com.android.rdc.mobilesafe.ui;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseToolBarActivity;
import com.android.rdc.mobilesafe.entity.ScanAppInfo;
import com.android.rdc.mobilesafe.util.IOUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import butterknife.BindView;

public class VirusScanActivity extends BaseToolBarActivity {
    private static final String TAG = "VirusScanActivity";
    @BindView(R.id.tv_scan_process)
    TextView mTvScanProcess;
    @BindView(R.id.iv_scanning_app_icon)
    ImageView mIvScanningAppIcon;
    @BindView(R.id.tv_scanning_app_name)
    TextView mTvScanningAppName;
    @BindView(R.id.rv)
    RecyclerView mRv;
    @BindView(R.id.btn_cancel_scanning)
    Button mBtnCancelScanning;

    public static final int BEGIN_SCANNING = 101;
    public static final int SCANNING = 102;
    public static final int COMPLETE_SCANNING = 103;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BEGIN_SCANNING:
                    mTvScanningAppName.setText("初始化杀毒引擎...");
                    break;
                case SCANNING:
                    ScanAppInfo scanAppInfo = (ScanAppInfo) msg.obj;
                    mTvScanningAppName.setText("正在扫描：" + scanAppInfo.getAppName());
                    //设置进度，
//                    int speed =

                    // 设置选中项最新扫描完成的项


                    break;
                case COMPLETE_SCANNING:
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    @Override
    protected int setResId() {
        return R.layout.activity_virus_scan;
    }

    @Override
    protected void initData() {
        copyDB("antivirus.db");
    }

    @Override
    protected void initView() {
        setTitle("病毒查杀");
    }

    @Override
    protected void initListener() {

    }

    /**
     * 将 asset 目录下的文件复制到 data 目录下
     */
    private void copyDB(final String dbName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(getFilesDir(), dbName);
                if (file.exists() && file.length() > 0) {
                    Log.i(TAG, "数据库已经存在");
                    return;
                }
                InputStream inputStream = null;
                OutputStream outputStream = null;
                try {
                    inputStream = getAssets().open(dbName);//原存储位置的输出流
                    outputStream = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, len);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    IOUtil.closeQuietly(inputStream);
                    IOUtil.closeQuietly(outputStream);
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);//清空消息，防止内存泄漏
        super.onDestroy();
    }
}
