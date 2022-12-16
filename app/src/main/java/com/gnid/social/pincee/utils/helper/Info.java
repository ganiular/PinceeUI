package com.gnid.social.pincee.utils.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.telephony.TelephonyManager;

public class Info {
    /** Get the country region such as NG, UK of the current location of the device network */
    public static String getCountryRegion(Context context){
        TelephonyManager tm;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            tm = context.getSystemService(TelephonyManager.class);
        } else {
            tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        }
        return tm.getNetworkCountryIso().toUpperCase();
    }

    public static int getDisplayHeight(Context context){
        return context.getResources().getDisplayMetrics().heightPixels;
    }
    public static int getDisplayWidth(Context context){
        return context.getResources().getDisplayMetrics().widthPixels;
    }
}
