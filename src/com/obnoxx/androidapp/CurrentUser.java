package com.obnoxx.androidapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

    public static void setUser(final Context appContext, User user) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            editor.putString("user", user.toJSONObject().toString());
            editor.commit();
        } catch (JSONException e) {
            Log.w(TAG, "Could not save user", e);
        }

        if (getRegistrationId(appContext) == null) {
            GetRegistrationIdTask t = new GetRegistrationIdTask(appContext) {
                @Override
                protected void onPostExecute(String registrationId) {
                    setRegistrationId(appContext, registrationId);
                }
            };
            t.execute();
        }
    }

    public static void setRegistrationId(Context appContext, String registrationId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("registrationId", registrationId);
        editor.commit();
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    public static String getRegistrationId(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String registrationId = prefs.getString("registrationId", null);
        if (registrationId == null) {
            Log.i(TAG, "Registration not found.");
            return null;
        }

        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt("appVersion", Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo =
                    context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}
