package com.example.adbets;

import android.content.Context;
import android.content.SharedPreferences;

public class TechSharedPref {
    private static TechSharedPref mInstance;
    private static final String SHARED_PREF_NAME = "TechSharedPref";
    private static Context mContext;

    private static final String TECH_NAME = "tech_name";

    private TechSharedPref(Context context) {
        this.mContext = context;
    }

    public static synchronized TechSharedPref getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TechSharedPref(context);
        }

        return mInstance;
    }

    public void setTechName(String techName) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TECH_NAME, techName);
        editor.apply();
    }

    public String getTechName() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(TECH_NAME, "");
    }

}
