package com.android.rdc.mobilesafe.ui;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.rdc.mobilesafe.R;
import com.android.rdc.mobilesafe.base.BaseToolBarActivity;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpNetworkInterceptActivity extends BaseToolBarActivity {


    @BindView(R.id.et)
    EditText mEt;
    @BindView(R.id.btn_send)
    Button mBtnSend;
    @BindView(R.id.tv_response)
    TextView mTvResponse;

    private OkHttpClient mOkHttpClient;

    private static final String KEY = "060ab7f7b45140b3a2236181228b06be";
    private static final String BASE_URL = "http://www.tuling123.com/openapi/api?key=";

    @Override
    protected int setResId() {
        return R.layout.activity_okhttp_network;
    }

    @Override
    protected void initData() {
        mOkHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }


    @OnClick(R.id.btn_send)
    public void onViewClicked() {
        String url = BASE_URL + KEY + "&info=" + mEt.getText();

        mOkHttpClient.newCall(
                new Request.Builder()
                        .get()
                        .url(url)
                        .build()
        ).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                updateUi("请求失败,因为" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String result = response.body().string();
                updateUi(result);
            }
        });

    }

    private void updateUi(final String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTvResponse.setText(result);
            }
        });
    }
}
