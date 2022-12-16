package com.gnid.social.pincee.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.gnid.social.pinceeui.R;

public class ErrorDialog {
    public ErrorDialog(Context context, int messageRes){
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.error)
                .setMessage(messageRes)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                });
        builder.show();
    }
}
