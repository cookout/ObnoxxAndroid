package com.obnoxx.androidapp;

import android.content.ContentValues;
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
                        sound.save(context);
                    }
                    for (SoundDelivery soundDelivery : response.getSoundDeliveries()) {
                        soundDelivery.save(context);
                    }
                    for (User user : response.getUsers()) {
                        db.insertWithOnConflict(DatabaseHandler.USER_TABLE_NAME, null,
                                user.toValues(), SQLiteDatabase.CONFLICT_REPLACE);
                        Log.w(TAG, "insert into " + DatabaseHandler.USER_TABLE_NAME +
                                " values (" +
                                GetSoundsOperation.this.toString(user.toValues()) + ")");
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