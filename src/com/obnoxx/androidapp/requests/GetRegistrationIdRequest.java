package com.obnoxx.androidapp.requests;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * This task registers this Android device with Google's GCM service to receive
 * push notifications.  The registration ID that GCM returns must be then passed
 * to our server so that it can talk to GCM and send messages to this device.
 *
 * To use this class, extend it to override {@code onPostExecute}.  The String
 * passed to it is the GCM registration ID for this device.
 */
public class GetRegistrationIdRequest extends AsyncTask<Void, Void, String> {
    private static final String TAG = "GetRegistrationIdTask";
    private static GoogleCloudMessaging sGcm;

    /**
     * This is the project number you got from the API Console, as described in "Getting Started."
     * Project ID = grounded-braid-655, project number = 984223128386.
     */
    private static final String SENDER_ID = "984223128386";

    private Context mContext;

    public GetRegistrationIdRequest(Context context) {
        mContext = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        String msg = "";
        try {
            if (sGcm == null) {
                sGcm = GoogleCloudMessaging.getInstance(mContext);
            }
            String registrationId = sGcm.register(SENDER_ID);
            msg = "Device registered, registration ID=" + registrationId;

            // Pass back the registration ID so that the person who invoked this task
            // can commit it to the server and shared preferences.
            return registrationId;

        } catch (IOException e) {
            Log.w(TAG, "GCM registration error :" + e.getMessage(), e);

        }
        return msg;
    }
}
