package com.android.rdc.mobilesafe.util;

import android.app.ProgressDialog;
import android.content.Context;

public final class ProgressDialogUtil {
    private ProgressDialogUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    private static ProgressDialog sProgressDialog;


    public static void showDefaultDialog(Context context) {
        showDialog(context, "正在加载数据，请稍候...");
    }

    public static void showDialog(Context context, CharSequence msg) {
        sProgressDialog = new ProgressDialog(context);
        sProgressDialog.setMessage(msg);
        sProgressDialog.setCanceledOnTouchOutside(false);
        sProgressDialog.setCancelable(true);
        sProgressDialog.show();
    }

    public static void showDialog(Context context, String title, CharSequence msg) {
        sProgressDialog = new ProgressDialog(context);
        sProgressDialog.setTitle(title);
        sProgressDialog.setMessage(msg);
        sProgressDialog.setCanceledOnTouchOutside(false);
        sProgressDialog.setCancelable(true);
        sProgressDialog.show();
    }


    public static void setMsg(CharSequence msg) {
        if (sProgressDialog != null) {
            sProgressDialog.setMessage(msg);
        }
    }

    public static void dismiss() {
        if (sProgressDialog != null && sProgressDialog.isShowing()) {
            sProgressDialog.dismiss();
        }
    }

}
