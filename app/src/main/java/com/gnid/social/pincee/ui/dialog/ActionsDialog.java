package com.gnid.social.pincee.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import com.gnid.social.pinceeui.R;

public abstract class ActionsDialog {
    public static final int TAKE_PICTURE = 1;
    public static final int SELECT_IMAGE = 2;

    public ActionsDialog(Activity activity){
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_actions, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setView(view);
        AlertDialog dialog = builder.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        view.findViewById(R.id.image_view_1).setOnClickListener(v -> onItemClick(dialog, TAKE_PICTURE));
        view.findViewById(R.id.image_view_2).setOnClickListener(v -> onItemClick(dialog, SELECT_IMAGE));

    }

    public abstract void onItemClick(AlertDialog dialog, int which);
}
