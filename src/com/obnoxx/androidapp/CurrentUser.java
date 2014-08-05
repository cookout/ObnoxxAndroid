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

    /**
     * Returns true if we believe the user's logged in.  (The possession of a session ID
     * implies this ... Though we might some day want logic to verify session IDs and
     * refresh them as necessary).
     */
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

        maybeFetchRegistrationId(appContext);
    }

    /**
     * Makes sure this device is registered with Google to receive Obnoxx
     * push notifications.
     * NOTE(jonemerson): This logic probably belongs somewhere else.
     * TODO(jonemerson): Make sure the server agrees that the registration ID
     *     stored in user prefs is valid.  Because if it doesn't, there ain't
     *     no notifications coming this way.
     */
    public static void maybeFetchRegistrationId(final Context appContext) {
        // If we don't have a registration ID, let's go get one for this device.
        if (getUser(appContext) != null &&
                getRegistrationId(appContext) == null) {
            new GetRegistrationIdTask(appContext) {
                @Override
                protected void onPostExecute(final String registrationId) {
                    // Now we have to save the registration ID to the backend, so
                    // that it can actually send us push notifications.
                    new AddDeviceRegistrationIdTask(appContext, registrationId) {
                        @Override
                        protected void onPostExecute(Boolean b) {
                            // Now that we know everything went hunky-dory,
                            // save the registration ID locally so that we
                            // know we've registered.
                            if (Boolean.TRUE.equals(b)) {
                                setRegistrationId(appContext, registrationId);
                            }
                        }
                    }.execute();
                }
            }.execute();
        }
    }

    public static void setRegistrationId(Context appContext, String registrationId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("registrationId", registrationId);
        editor.putInt("appVersion", getAppVersion(appContext));
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
    public static String getRegistrationId(Context appContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
        String registrationId = prefs.getString("registrationId", null);
        if (registrationId == null) {
            Log.i(TAG, "GCM registration not found.");
            return null;
        }

        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt("appVersion", Integer.MIN_VALUE);
        int currentVersion = getAppVersion(appContext);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed - discarding existing GCM registration ID.");
            return null;
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
