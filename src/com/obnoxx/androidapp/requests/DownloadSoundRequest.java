package com.obnoxx.androidapp.requests;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.obnoxx.androidapp.SoundRecorder;
import com.obnoxx.androidapp.data.Sound;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Makes sure that the passed sound has a local representation (e.g. its stored
 * to disk).  If it does not, download it, update the database, and update the
 * object.
 *
 * Returns true if the file was successfully downloaded and stored to local
 * disk.
 */
public class DownloadSoundRequest extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "DownloadSoundTask";

    private final Context mContext;
    private final Sound mSound;

    public DownloadSoundRequest(Context context, Sound sound) {
        mContext = context;
        mSound = sound;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        if (mSound.getData().getLocalFilePath() != null) {
            return true;
        }

        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(mSound.getData().getSoundFileUrl());

        HttpResponse response;
        try {
            response = client.execute(get);

            // Store the file locally.
            String filename = SoundRecorder.getNewFilename();
            InputStream input = response.getEntity().getContent();
            FileOutputStream outputStream = new FileOutputStream(filename);
            byte[] buffer = new byte[10000];
            int readBytes = input.read(buffer, 0, buffer.length);
            while (readBytes > 0) {
                outputStream.write(buffer, 0, readBytes);
                readBytes = input.read(buffer, 0, buffer.length);
            }
            outputStream.close();

            // Update the object and the database.
            mSound.setLocalFilePath(mContext, filename);
            return true;

        } catch (IOException e) {
            Log.e(TAG, "Could not download file", e);
            return false;
        }
    }
}
