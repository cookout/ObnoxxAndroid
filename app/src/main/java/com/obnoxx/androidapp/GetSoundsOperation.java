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
    private final Context mContext;

    public GetSoundsOperation(final Context context) {
        mContext = context.getApplicationContext();
    }

    public void execute() {
        new GetSoundsRequest(mContext) {
            @Override
            public void onPostExecute(GetSoundsResponse response) {
                if (response.getStatusCode() == 200) {
                    DatabaseHandler dbHandler = new DatabaseHandler(mContext);
                    SQLiteDatabase db = dbHandler.getWritableDatabase();

                    for (Sound sound : response.getSounds()) {
                        sound.save(mContext);
                    }
                    for (SoundDelivery soundDelivery : response.getSoundDeliveries()) {
                        soundDelivery.save(mContext);
                    }
                    for (User user : response.getUsers()) {
                        user.save(mContext);
                    }
                } else {
                    Log.e(TAG, "Could not load sounds");
                }

                mContext.getContentResolver().notifyChange(
                        SoundDeliveryProvider.getUriForCurrentUserSoundDelivieries(mContext), null);
            }
        }.execute();
    }
}