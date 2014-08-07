package com.obnoxx.androidapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.obnoxx.androidapp.data.DatabaseHandler;
import com.obnoxx.androidapp.data.Sound;
import com.obnoxx.androidapp.data.SoundDelivery;
import com.obnoxx.androidapp.data.User;
import com.obnoxx.androidapp.requests.GetSoundsRequest;
import com.obnoxx.androidapp.requests.GetSoundsResponse;

/**
 * Does air-traffic control for getting all the sounds the user has sent or
 * received, plus their delivery details, plus any users involved, and then
 * persisting them in the SQLite database.
 */
public class GetSoundsOperation {
    private static final String TAG = "GetSoundsOperation";

    public GetSoundsOperation(final Context context) {
        new GetSoundsRequest(context) {
            @Override
            public void onPostExecute(GetSoundsResponse response) {
                if (response.getStatusCode() == 200) {
                    DatabaseHandler dbHandler = new DatabaseHandler(context);
                    SQLiteDatabase db = dbHandler.getWritableDatabase();

                    for (Sound sound : response.getSounds()) {
                        sound.save(context);
                    }
                    for (SoundDelivery soundDelivery : response.getSoundDeliveries()) {
                        soundDelivery.save(context);
                    }
                    for (User user : response.getUsers()) {
                        user.save(context);
                    }
                } else {
                    Log.e(TAG, "Could not load sounds");
                }

                onComplete(response);
            }
        }.execute();
    }

    private String toString(ContentValues v) {
        StringBuilder b = new StringBuilder();
        for (String key : v.keySet()) {
            b.append(key + "=\"" + v.getAsString(key) + "\" ");
        }
        return b.toString().trim();
    }

    public void onComplete(GetSoundsResponse response) {
        // Override this if you care.
    }
}