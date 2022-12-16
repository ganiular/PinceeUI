package com.gnid.social.pincee.utils.system.service;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class MyKeyboardManager {
    public static void requestKeyboardDelay(Context context, EditText editText, long delay){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                editText.removeCallbacks(this);
            }
        };
        editText.requestFocus();
        editText.postDelayed(runnable, delay);
    }

    public static void requestKeyboard(Context context, EditText editText){
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }
}
