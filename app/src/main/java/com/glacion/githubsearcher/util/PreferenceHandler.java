package com.glacion.githubsearcher.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.glacion.githubsearcher.R;

public class PreferenceHandler {
    private final SharedPreferences preferences;
    private final String SORT_KEY;
    private final String REVERSE_KEY;

    /**
     * Encapsulates the Shared Preferences for this app.
     * @param context Context object to be used for acquiring the preferences.
     */
    public PreferenceHandler(Context context) {
        String preferenceKey = context.getString(R.string.preference_file_key);
        Context appContext = context.getApplicationContext();
        preferences = appContext.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE);
        SORT_KEY = appContext.getString(R.string.repo_sort_key);
        REVERSE_KEY = appContext.getString(R.string.repo_reverse_key);
    }

    public void setSort(String value) {
        preferences.edit().putString(SORT_KEY, value).apply();
    }

    public String getSort() {
        String sortDefault = "stars";
        return preferences.getString(SORT_KEY, sortDefault);
    }

    public void setReverse(boolean value) {
        preferences.edit().putBoolean(REVERSE_KEY, value).apply();
    }

    public boolean getReverse() {
        return getBoolean(REVERSE_KEY);
    }

    private boolean getBoolean(String key){
        return preferences.getBoolean(key, false);
    }

}
