package com.obnoxx.androidapp.requests;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class VerifyPhoneNumberRequest extends AsyncTask<Void, Void, VerifyPhoneNumberResponse> {
    private static final String URL = "http://www.obnoxx.co/verifyPhoneNumber";

    private final Context mContext;
    private String mPhoneNumber = null;
    private String mVerificationCode = null;
    private String mTemporaryUserCode = null;

    public VerifyPhoneNumberRequest(Context context, String phoneNumber) {
        mContext = context;
        mPhoneNumber = phoneNumber;
    }

    public VerifyPhoneNumberRequest(Context context, String verificationCode,
                                    String temporaryUserCode) {
        mContext = context;
        mVerificationCode = verificationCode;
        mTemporaryUserCode = temporaryUserCode;
    }

    @Override
    protected VerifyPhoneNumberResponse doInBackground(Void... params) {
        ContentType foo = ContentType.APPLICATION_ATOM_XML;

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(URL);
        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        if (mPhoneNumber != null) {
            postParameters.add(new BasicNameValuePair("phoneNumber", mPhoneNumber));
        }
        if (mVerificationCode != null) {
            postParameters.add(new BasicNameValuePair("verificationCode", mVerificationCode));
        }
        if (mTemporaryUserCode != null) {
            postParameters.add(new BasicNameValuePair("temporaryUserCode", mTemporaryUserCode));
        }
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
            return (response.getStatusLine().getStatusCode() == 200) ?
                    new VerifyPhoneNumberResponse(responseString) :
                    new VerifyPhoneNumberResponse(response.getStatusLine().getStatusCode());
        } catch (IOException e) {
            return new VerifyPhoneNumberResponse(500);
        }
    }
}
