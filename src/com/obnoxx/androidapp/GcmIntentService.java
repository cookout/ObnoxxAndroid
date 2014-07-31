package com.obnoxx.androidapp;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
                        JSONObject newSound = new JSONObject(extras.getString("sound"));
                        new Sound(downloadSoundFile(newSound.getString("soundFileUrl"))).play();
                        addNewSoundNotification(null);
                    } catch (JSONException e) {
                        Log.w(TAG, "Could not parse new sound JSON", e);
                    }
                }
            }
        }

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    /**
     * Downloads the given file to local storage then returns the absolute path to its new
     * home.
     * TODO(jonemerson): Find a better place to do this.
     */
    private String downloadSoundFile(String soundFileUrl) {
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(soundFileUrl);

        HttpResponse response;
        try {
            response = client.execute(get);

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
            return filename;

        } catch (IOException e) {
            Log.e(TAG, "Could not download file", e);
            return null;
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