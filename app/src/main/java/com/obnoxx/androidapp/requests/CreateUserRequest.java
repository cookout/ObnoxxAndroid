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

public class CreateUserRequest extends AsyncTask<Void, Void, CreateUserResponse> {
    private static final String URL = "http://www.obnoxx.co/createUser";

    private final Context mContext;
    private String mUserName = null;
    private String mVerificationCode = null;
    private String mTemporaryUserCode = null;

    public CreateUserRequest(Context context, String userName, String verificationCode,
            String temporaryUserCode) {
        mContext = context;
        mUserName = userName;
        mVerificationCode = verificationCode;
        mTemporaryUserCode = temporaryUserCode;
    }

    @Override
    protected CreateUserResponse doInBackground(Void... params) {
        ContentType foo = ContentType.APPLICATION_ATOM_XML;

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(URL);
        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("userName", mUserName));
        postParameters.add(new BasicNameValuePair("verificationCode", mVerificationCode));
        postParameters.add(new BasicNameValuePair("temporaryUserCode", mTemporaryUserCode));
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
                    new CreateUserResponse(responseString) :
                    new CreateUserResponse(response.getStatusLine().getStatusCode());
        } catch (IOException e) {
            return new CreateUserResponse(500);
        }
    }
}
