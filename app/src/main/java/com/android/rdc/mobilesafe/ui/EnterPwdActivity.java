package com.android.rdc.mobilesafe.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseScrollTbActivity;
import com.android.rdc.mobilesafe.constant.Constant;
import com.android.rdc.mobilesafe.util.MD5Utils;

import butterknife.BindView;
import butterknife.OnClick;


public class EnterPwdActivity extends BaseScrollTbActivity {


    @BindView(R.id.et_pwd)
    EditText mEtPwd;
    @BindView(R.id.iv_btn)
    ImageView mIvBtn;
    @BindView(R.id.ll_pwd)
    LinearLayout mLlPwd;

    private SharedPreferences mSharedPreferences;
    private String mPwd;
    private String mPackageName;

    private PackageManager mPackageManager;

    @Override
    protected int setResId() {
        return R.layout.activity_enter_pwd;
    }

    @Override
    protected void initData() {
        mSharedPreferences = getSharedPreferences(Constant.SP_NAME_CONFIG, MODE_PRIVATE);
        // TODO: 2017/7/5 0005 临时密码
        mSharedPreferences.edit().putString(Constant.KEY_LOCK_PWD, MD5Utils.encode("123")).apply();

        mPwd = mSharedPreferences.getString(Constant.KEY_LOCK_PWD, null);
        Intent intent = getIntent();
        mPackageName = intent.getStringExtra(Constant.KEY_EXTRA_PACKAGE_NAME);
        //取图标、应用名 用的，这里不使用
        mPackageManager = getPackageManager();

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

    @OnClick(R.id.iv_btn)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_btn:
                String inputPwd = mEtPwd.getText().toString();
                if (TextUtils.isEmpty(inputPwd)) {
                    startAnim();
                    showToast("请输入密码");
                    return;
                } else {
                    // TODO: 2017/7/5 0005  以同样的加密算法加密，看结果是否一样，而不是取出存储的密码进行匹配
                    if (MD5Utils.encode(inputPwd).equals(mPwd)) {
                        Intent intent = new Intent();
                        intent.setAction(Constant.FILTER_ACTION);
                        intent.putExtra(Constant.KEY_EXTRA_PACKAGE_NAME, mPackageName);
                        sendBroadcast(intent);

                        Intent intent1 = mPackageManager.getLaunchIntentForPackage(mPackageName);
//                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent1.addCategory(Intent.CATEGORY_LAUNCHER);
                        startActivity(intent1);
                        finish();
                    } else {
                        startAnim();
                        showToast("密码错误");
                        return;
                    }
                }

                break;
        }
    }

    private void startAnim() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake);
        mLlPwd.startAnimation(animation);
    }

    @Override
    public void showPre() {
        startActivityAndFinishSelf(TrafficSettingActivity.class);
    }

    @Override
    public void showNext() {
        showToast("已经是最后一个了");
    }
}
