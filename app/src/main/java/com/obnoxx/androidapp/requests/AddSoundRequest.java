package com.obnoxx.androidapp.requests;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.obnoxx.androidapp.CurrentUser;
import com.obnoxx.androidapp.data.Sound;

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

/**
 * This task handles the uploading of a {@code sound} to the Cookout server,
 * which will then inform {@code phoneNumber} that there is a sound for them
 * to hear.
 */
public class AddSoundRequest extends AsyncTask<Void, Void, String> {
    private static final String URL = "http://www.obnoxx.co/addSound";

    private final Context mContext;
    private final Sound mSound;
    private final String mPhoneNumber;

    // TODO(jonemerson): The API for this class should NOT take a Sound.  A Sound should only be
    // created on the server-side.  This constructor should just take the sound file and recipient,
    // then update the database with the Sound it gets back from the server.
    public AddSoundRequest(Context context, Sound sound, String phoneNumber) {
        mContext = context;
        mSound = sound;
        mPhoneNumber = phoneNumber;
    }

    @Override
    protected String doInBackground(Void... params) {
        ContentType foo = ContentType.APPLICATION_ATOM_XML;

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(URL);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addPart("soundFile", new FileBody(new File(mSound.getData().getLocalFilePath())));
        builder.addTextBody("phoneNumber", mPhoneNumber);
        builder.addTextBody("sessionId", CurrentUser.getSessionId(mContext));
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
        Toast.makeText(mContext, data, Toast.LENGTH_SHORT).show();
    }
}
