package com.gnid.social.pincee.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.widget.Toast;

public class MyClipboardManager {
    public static void copyTextToClipboard(Context context, CharSequence text){
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("", text);
        clipboard.setPrimaryClip(clipData);
        // Only show a toast for Android 12 and lower. android 13 and higher has is way of notifying user
        // say if Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2
        if (Build.VERSION.SDK_INT <= 33)
            Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show();
    }
}
