package com.chauduong.longleg;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private static final String SHARED_PREFERENCES_NAME = "image_index";
    private static final String INDEX_KEY = "index_key";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static SharedPreferencesManager instance;

    private SharedPreferencesManager(Context mContext) {
        sharedPreferences = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static SharedPreferencesManager getInstance(Context mContext) {
        if (instance == null) instance = new SharedPreferencesManager(mContext);
        return instance;
    }

    public int getIndex() {
        int index = sharedPreferences.getInt(INDEX_KEY, 0);
        editor.putInt(INDEX_KEY, (index + 1));
        editor.commit();
        return index;
    }
}
