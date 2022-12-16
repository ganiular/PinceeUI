package com.gnid.social.pincee.utils.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefsHelper {
    private static final String KEY_COUNTRY_REGION = "country_code";
    private final SharedPreferences prefs;
    private static PrefsHelper instance;

    private PrefsHelper(Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    static public PrefsHelper getInstance(Context context){
        if(instance == null) instance = new PrefsHelper(context);
        return instance;
    }

    public  String getCountryRegion(){
        return prefs.getString(KEY_COUNTRY_REGION, null);
    }
    public void setCountryRegion(String value){
        writeString(KEY_COUNTRY_REGION, value);
    }

    private void writeString(String key, String value){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return prefs.getBoolean(key, defValue);
    }

    public void setBoolean(String key, boolean value){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
}
