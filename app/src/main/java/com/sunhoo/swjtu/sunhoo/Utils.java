package com.sunhoo.swjtu.sunhoo;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by tangpeng on 2017/7/20.
 */

public class Utils {

    public static ProgressDialog getProgressDialog(Context context, String title, String msg) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage(msg);
        if (title != null)
            dialog.setTitle(title);
        dialog.create();
        return dialog;
    }

}
