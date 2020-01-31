package com.hackdroid.droidrecord.Util;

import android.app.Activity;
import android.content.Context;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ShowAlert {
    Activity activity;
    Context context;

    public ShowAlert(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    public void alert(String msg) {
        new MaterialAlertDialogBuilder(context).setMessage(msg)
                .setPositiveButton("OK", null)
                .show();

    }
}
