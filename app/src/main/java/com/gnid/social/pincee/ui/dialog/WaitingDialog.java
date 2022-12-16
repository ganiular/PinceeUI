package com.gnid.social.pincee.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.gnid.social.pinceeui.R;

import java.util.Objects;

public class WaitingDialog extends DialogFragment{
    public static final String TAG = WaitingDialog.class.getSimpleName();
    private View dialogView;
    private int preMessageRes = 0;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // use the Builder class to create dialog
        Log.i(TAG, "onCreateDialog: ");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_waiting, null);
        builder.setView(dialogView);
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if(preMessageRes != 0){
            ((TextView) dialogView.findViewById(R.id.text_view)).setText(preMessageRes);
        }
        return dialog;
    }

    public void setMessage(int messageRes){
        Log.i(TAG, "setMessage: visible:"+isVisible());
        if(isVisible()){
            ((TextView) dialogView.findViewById(R.id.text_view)).setText(preMessageRes);
        } else {
            preMessageRes = messageRes;
        }
    }

    public void startProgress(FragmentManager fragmentManager, String tag, int messageRes){
        show(fragmentManager, tag);
        setMessage(messageRes);
    }
}
