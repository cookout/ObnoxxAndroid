package com.obnoxx.androidapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;

/**
 * Stores information that we know about the current user into Android's preferences API for
 * retrieval across application invocations / rotates.
 */
public class CurrentUser {
    private static final String TAG = "CurrentUser";

    public static boolean hasSessionId(Context appContext) {
        return getSessionId(appContext) != null;
    }

    public static String getSessionId(Context appContext) {
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(appContext);
        return sharedPrefs.getString("sessionId", null);
    }

    public static User getUser(Context appContext) {
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(appContext);
        String userJson = sharedPrefs.getString("user", null);
        try {
            return userJson == null ? null : new User(userJson);
        } catch (JSONException e) {
            Log.w(TAG, "Could not parse saved User", e);
            return null;
        }
    }

    public static void setSessionId(Context appContext, String sessionId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("sessionId", sessionId);
        editor.commit();
    }

    public static void setUser(Context appContext, User user) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            editor.putString("user", user.toJSONObject().toString());
            editor.commit();
        } catch (JSONException e) {
            Log.w(TAG, "Could not save user", e);
        }
    }
}
