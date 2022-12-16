package com.gnid.social.pincee.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

import com.gnid.social.pinceeui.R;

public abstract class MessageDeleteDialog {
    protected static final int CANCELED = 0;
    protected static final int DELETE_FOR_ME = 1;
    protected static final int DELETE_FOR_ALL = 2;

    public MessageDeleteDialog(Activity activity, int count){
        View dialogView = activity.getLayoutInflater().
                inflate(R.layout.dialog_delete_message, null);
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setView(dialogView)
                .show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setOnCancelListener(this::onCancel);
        TextView textView = dialogView.findViewById(R.id.text_view);
        textView.setText(activity.getResources().getQuantityString(R.plurals.delete_message, count, count));
        dialogView.findViewById(R.id.button_1).setOnClickListener(v -> onItemClick(dialog, DELETE_FOR_ME));
        dialogView.findViewById(R.id.button_2).setOnClickListener(v -> onItemClick(dialog, DELETE_FOR_ALL));
        dialogView.findViewById(R.id.button_3).setOnClickListener(v -> onItemClick(dialog, CANCELED));
    }

    protected abstract void onCancel(DialogInterface dialog);

    public abstract void onItemClick(AlertDialog dialog, int which);
}
