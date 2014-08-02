package com.obnoxx.androidapp;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

/**
 * This service handles incoming Android/GCM push notifications, such as
 * when the user receives a sound.
 */
public class GcmIntentService extends IntentService {
    private static final String TAG = "GcmIntentService";

    public static final int NOTIFICATION_ID = 1;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.w(TAG, "Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.w(TAG, "Deleted messages on server: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Log.i(TAG, "Received: " + extras.toString());
                if ("newSound".equals(extras.getString("type"))) {
                    try {
                        Bundle soundBundle = extras.getBundle("sound");
                        String s = extras.getString("sound");
                        Sound sound = new Sound(s);
                        cacheSoundFileLocally(sound);
                        sound.play();
                        addNewSoundNotification(null);
                    } catch (JSONException e) {
                        Log.w(TAG, "Could not parse new sound JSON date", e);
                    }
                }
            }
        }

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    /**
     * Makes sure that the passed sound has a local representation (e.g. its
     * stored to disk).  If it does not, download it, update the database,
     * and update the object.
     * TODO(jonemerson): Find a better place to do this.
     */
    private void cacheSoundFileLocally(Sound sound) {
        if (sound.getLocalFilePath() != null) {
            return;
        }

        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(sound.getSoundFileUrl());

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
            sound.setLocalFilePath(filename);
            SQLiteDatabase db = new DatabaseHandler(this).getWritableDatabase();
            db.replace(DatabaseHandler.SOUND_TABLE_NAME, null, sound.toValues());

        } catch (IOException e) {
            Log.e(TAG, "Could not download file", e);
        }
    }

    /**
     * Puts the message into a notification and post it.
     */
    private void addNewSoundNotification(Sound sound) {
        String msg = "You received a sound. Click to play.";

        // TODO(jonemerson): The content intent should go to the PlaybackActivity, with an Extra
        // indicating that it should stay playing immediately.
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Obnoxx")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg)
                .setContentIntent(PendingIntent.getActivity(this, 0,
                        new Intent(this, InitActivity.class), 0));

        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}