package com.obnoxx.androidapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by jon on 7/30/14.
 */
public class CurrentUser {
    public static boolean hasSessionId(Context appContext) {
        String sessionId = getSessionId(appContext);
        return getSessionId(appContext) != null;
    }

    public static String getSessionId(Context appContext) {
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(appContext);
        return sharedPrefs.getString("sessionId", null);
    }

    public static void setSessionId(Context appContext, String sessionId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("sessionId", sessionId);
        editor.commit();
    }
}
