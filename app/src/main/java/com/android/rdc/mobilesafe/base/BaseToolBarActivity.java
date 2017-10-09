package com.android.rdc.mobilesafe.base;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.android.rdc.mobilesafe.R;

public abstract class BaseToolBarActivity extends BaseActivity {

    private Toolbar mToolbar;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.setContentView(layoutResID);
        initToolbar();
    }

    private void initToolbar() {
        mToolbar = $(R.id.toolbar);
        if (mToolbar == null) {
            throw new IllegalStateException("No Toolbar!");
        }

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowTitleEnabled(false);//不显示默认标题
    }

    protected Toolbar getToolbar() {
        return mToolbar;
    }

    /**
     * 设置标题
     */
    public void setTitle(CharSequence title) {
        mToolbar.setTitle(title);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
