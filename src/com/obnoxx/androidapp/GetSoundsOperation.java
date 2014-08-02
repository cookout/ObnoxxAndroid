package com.obnoxx.androidapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Does air-traffic control for getting all the sounds the user has sent or
 * received, plus their delivery details, plus any users involved, and then
 * persisting them in the SQLite database.
 */
public class GetSoundsOperation {
    private static final String TAG = "GetSoundsOperation";

    public GetSoundsOperation(final Context context) {
        new GetSoundsTask(context) {
            @Override
            public void onPostExecute(GetSoundsResponse response) {
                if (response.getStatusCode() == 200) {
                    DatabaseHandler dbHandler = new DatabaseHandler(context);
                    SQLiteDatabase db = dbHandler.getWritableDatabase();

                    for (Sound sound : response.getSounds()) {
                        db.replace(DatabaseHandler.SOUND_TABLE_NAME, null, sound.toValues());
                    }
                    for (SoundDelivery soundDelivery : response.getSoundDeliveries()) {
                        db.replace(DatabaseHandler.SOUND_DELIVERY_TABLE_NAME, null,
                                soundDelivery.toValues());
                    }
                    for (User user : response.getUsers()) {
                        db.replace(DatabaseHandler.USER_TABLE_NAME, null, user.toValues());
                    }
                } else {
                    Log.e(TAG, "Could not load sounds");
                }
            }
        }.execute();
    }
}