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
import com.obnoxx.androidapp.data.Sound;
import com.obnoxx.androidapp.data.SoundData;
import com.obnoxx.androidapp.data.SoundDelivery;
import com.obnoxx.androidapp.data.SoundDeliveryData;
import com.obnoxx.androidapp.requests.DownloadSoundRequest;
import com.obnoxx.androidapp.ui.InitActivity;

import org.json.JSONException;

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
                        final Sound sound = new Sound(new SoundData(extras.getString("sound")));
                        final SoundDelivery soundDelivery = new SoundDelivery(
                                new SoundDeliveryData(extras.getString("soundDelivery")));

                        // If we already know about this delivery, do nothing.  We've already played
                        // the sound and recorded to storage.
                        synchronized (this) {
                            if (SoundDelivery.get(this.getApplicationContext(),
                                    soundDelivery.getData().getId()) != null) {
                                return;
                            }
                            sound.save(this.getApplicationContext());
                            soundDelivery.save(this.getApplicationContext());
                        }

                        new DownloadSoundRequest(this.getApplicationContext(), sound) {
                            @Override
                            public void onPostExecute(Boolean success) {
                                if (success) {
                                    sound.play();
                                    addNewSoundNotification(null);
                                }
                            }
                        }.execute();
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
     * Puts the message into a notification and posts it to the System's
     * notifications tray.
     */
    private void addNewSoundNotification(SoundData soundData) {
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