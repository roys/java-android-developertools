package com.roysolberg.android.developertools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.preference.PreferenceManager;

public class AppSettings {

    protected final static String KEY_SHOW_RESOURCE_QUALIFIERS_COLLAPSED = "show_resource_qualifiers_collapsed";

    protected SharedPreferences sharedPreferences;
    protected Editor editor;
    protected static AppSettings appSettings;

    protected AppSettings(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static AppSettings getInstance(Context context) {
        if (appSettings == null) {
            appSettings = new AppSettings(context);
        }
        return appSettings;
    }

    @SuppressLint("CommitPrefEdits")
    protected Editor getEditor() {
        if (editor == null) {
            editor = sharedPreferences.edit();
        }
        return editor;
    }

    public boolean showResourceQualifiersCollapsed() {
        return sharedPreferences.getBoolean(KEY_SHOW_RESOURCE_QUALIFIERS_COLLAPSED, true);
    }

    public void setShowResourceQualifiersCollapsed(boolean folded) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            getEditor().putBoolean(KEY_SHOW_RESOURCE_QUALIFIERS_COLLAPSED, folded).apply();
        } else {
            getEditor().putBoolean(KEY_SHOW_RESOURCE_QUALIFIERS_COLLAPSED, folded).commit();
        }
    }

}
