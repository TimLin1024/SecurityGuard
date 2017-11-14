package com.android.rdc.mobilesafe.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;

public final class ProgressDialogUtil {
    private ProgressDialogUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    private static ProgressDialog sProgressDialog;


    public static void showDefaultDialog(Context context) {
        if (sProgressDialog == null) {//避免重复创建
            showDialog(context, "正在加载数据，请稍候...");
        }
    }

    public static void showDialog(Context context, CharSequence msg) {
        if (sProgressDialog != null) {//避免重复创建
            setMsg(msg);
            return;
        }
        showDialog(context, null, msg);
    }

    public static void showDialog(Context context, String title, CharSequence msg) {
        if (sProgressDialog != null) {//避免重复创建
            setMsg(msg);
            return;
        }
        sProgressDialog = new ProgressDialog(context);
        sProgressDialog.setTitle(title);
        sProgressDialog.setMessage(msg);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sProgressDialog.setProgressStyle(android.R.style.Widget_Material_ProgressBar);
        }
//        sProgressDialog.setCanceledOnTouchOutside(false);
        sProgressDialog.setCancelable(true);
        sProgressDialog.show();
    }


    public static void setMsg(CharSequence msg) {
        if (sProgressDialog != null) {
            sProgressDialog.setMessage(msg);
        }
    }

    public static void dismiss() {
        if (sProgressDialog != null) {
            sProgressDialog.dismiss();
            sProgressDialog = null;
        }
    }

}
