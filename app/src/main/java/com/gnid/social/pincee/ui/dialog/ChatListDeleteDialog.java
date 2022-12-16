package com.gnid.social.pincee.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.gnid.social.pinceeui.R;

public abstract class ChatListDeleteDialog {
    protected static final int CANCELED = 0;
    protected static final int DELETE_WITH_MEDIA = 1;
    protected static final int DELETE_WITHOUT_MEDIA = 2;

    public ChatListDeleteDialog(Activity activity, int count){
        View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_delete_chat, null);
        CheckBox checkBox = dialogView.findViewById(R.id.check_box);
        String checkBoxText = activity.getResources().getQuantityString(R.plurals.also_delete_it_media_files_from_phone_gallery, count);
        TextView textView = dialogView.findViewById(R.id.text_view);
        textView.setText(activity.getResources().getQuantityString(R.plurals.delete_chat, count, count));
        checkBox.setText(checkBoxText);
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setView(dialogView)
                .show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setOnCancelListener(this::onCanceled);
        dialogView.findViewById(R.id.button_1).setOnClickListener(v ->
                onItemClick(dialog, checkBox.isChecked()? DELETE_WITH_MEDIA: DELETE_WITHOUT_MEDIA));
        dialogView.findViewById(R.id.button_3).setOnClickListener(v -> onItemClick(dialog, CANCELED));
    }

    protected abstract void onCanceled(DialogInterface dialog);

    public abstract void onItemClick(DialogInterface dialog, int which);
}
