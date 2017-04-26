package com.jcedar.chartbills.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.jcedar.chartbills.R;

/**
 * Created by OLUWAPHEMMY on 3/15/2017.
 */

public class PrefUtils {


//    private static final String UPDATE_TIME_KEY = "update_time_key";

    public static void setUpdateTime(Context context, String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(context.getResources().getString(R.string.pref_set_reminder_time), key).apply();
    }

    public static String getUpdateTime(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(context.getResources().getString(R.string.pref_set_reminder_time), "0");
    }
}
