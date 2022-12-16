package com.gnid.social.pincee.utils.edit;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.gnid.social.pinceeui.R;

public class ChatMessageTextWatcher implements TextWatcher {
    private final View btnCamera;
    private boolean isCameraButtonVisible = true;
    public ChatMessageTextWatcher(Activity activity){
        View layout = activity.findViewById(R.id.message_input_bar);
        btnCamera = layout.findViewById(R.id.button_camera);
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s.length() == 0 && !isCameraButtonVisible) {
            btnCamera.setVisibility(View.VISIBLE);
            isCameraButtonVisible = true;
        }
        else if(s.length() > 0 && isCameraButtonVisible){
            btnCamera.setVisibility(View.GONE);
            isCameraButtonVisible = false;
        }
    }
}
