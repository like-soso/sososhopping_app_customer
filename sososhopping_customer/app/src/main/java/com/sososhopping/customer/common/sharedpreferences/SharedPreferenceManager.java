package com.sososhopping.customer.common.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.sososhopping.customer.common.Constant;

public class SharedPreferenceManager {

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(Constant.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static void setString(Context context, String key, String value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        return prefs.getString(key, Constant.SHARED_PREFERENCE_DEFAULT_STRING);
    }

    public static void deleteString(Context context, String key){
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.commit();
    }
}