package com.obnoxx.androidapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * This task informs CookoutServer that this Android device has registered
 * itself for GCM locations and the registration ID it got from GCM was
 * {@code registrationId}.  CookoutServer then records that registration ID
 * and uses it to send this user push notifications (e.g. sounds for them).
 */
public class AddDeviceRegistrationIdTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "AddDeviceRegistrationIdTask";
    private static final String URL = "http://www.obnoxx.co/addDeviceRegistrationId";

    private final Context mContext;
    private final String mRegistrationId;

    public AddDeviceRegistrationIdTask(Context context, String registrationId) {
        mContext = context;
        mRegistrationId = registrationId;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(URL);
        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("sessionId", CurrentUser.getSessionId(mContext)));
        postParameters.add(new BasicNameValuePair("registrationId", mRegistrationId));
        postParameters.add(new BasicNameValuePair("type", "android"));
        try {
            post.setEntity(new UrlEncodedFormEntity(postParameters));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        HttpResponse response;
        try {
            response = client.execute(post);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            if (responseString.startsWith("&&&PREFIX&&&")) {
                responseString = responseString.substring("&&&PREFIX&&&".length());
            }
            return response.getStatusLine().getStatusCode() == 200;
        } catch (IOException e) {
            Log.w(TAG, "Android registration ID error", e);
            return false;
        }
    }
}
