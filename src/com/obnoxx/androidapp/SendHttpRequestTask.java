package com.obnoxx.androidapp;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;

public class SendHttpRequestTask extends AsyncTask<Void, Void, String> {
    private static final String URL = "http://www.obnoxx.co/addSound";

    private final Context mContext;
    private final String mFileName;
    private final int mCurrentFormat; // TODO(jonemerson): Figure out what we're trying to do here.
    private final String mPhoneNumber;

    public SendHttpRequestTask(Context context, String fileName, int currentFormat,
            String phoneNumber) {
        mContext = context;
        mFileName = fileName;
        mCurrentFormat = currentFormat;
        mPhoneNumber = phoneNumber;
    }

    @Override
    protected String doInBackground(Void... params) {
        ContentType foo = ContentType.APPLICATION_ATOM_XML;

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(URL);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addPart("soundFile", new FileBody(new File(mFileName)));
        builder.addTextBody("phoneNumber", mPhoneNumber);
        builder.addTextBody("sessionId",
                "9tvQvzoXH1dPSPBCkaCRHZ0se_Cjo8TYBKOGgdN0wRym0vbD1fwN4lItaAmPFAnG");
        post.setEntity(builder.build());

        HttpResponse response;
        try {
            response = client.execute(post);
        } catch (IOException e) {
            // TODO(jonemerson): Add error handling.
            throw new RuntimeException(e);
        }

        return response.getStatusLine().getStatusCode() == 200 ? "ok" : "error";
    }

    @Override
    protected void onPostExecute(String data) {
        Toast.makeText(mContext, data, Toast.LENGTH_SHORT).show();;
    }
}
