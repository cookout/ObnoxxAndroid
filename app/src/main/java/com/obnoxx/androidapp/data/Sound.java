package com.obnoxx.androidapp.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.util.Log;

import com.obnoxx.androidapp.DateHelper;

import java.io.File;
import java.io.IOException;

/**
 * Business logic for sounds.  Deals with retrieving sounds, playing sounds,
 * modifying sounds, etc.  Uses the sound fields stored in a {@code SoundData}.
 */
public class Sound {
    private static final String TAG = "Sound";
    private static MediaPlayer mPlayer = null;
    private final SoundData mSoundData;

    public Sound(SoundData soundData) {
        mSoundData = soundData;
    }

    public SoundData getData() {
        return mSoundData;
    }

    public void setLocalFilePath(Context context, String localFilePath) {
        mSoundData.setLocalFilePath(localFilePath);
        save(context);
    }

    public void play() {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        } else {
            mPlayer.reset();
        }

        try {
            try {
                mPlayer.setDataSource(mSoundData.getLocalFilePath());
            } catch (IOException e) {
                Log.e(TAG, "Could not play sound, it is corrupted.  File size = " +
                        new File(mSoundData.getLocalFilePath()).length());
                return;
            }
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(Context context) {
        SQLiteDatabase db = new DatabaseHandler(context).getWritableDatabase();
        db.insertWithOnConflict(SoundData.SQL_TABLE_NAME, null, mSoundData.toValues(),
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    public static Sound get(Context context, String soundId) {
        SQLiteDatabase db = new DatabaseHandler(context).getReadableDatabase();
        String[] columns = {
                SoundData.SQL_ID,
                SoundData.SQL_USER_ID,
                SoundData.SQL_FILE_URL,
                SoundData.SQL_LOCAL_FILE_PATH,
                SoundData.SQL_CREATE_DATE_TIME,
        };
        String[] selectionArgs = new String[] {
                soundId
        };
        Cursor cursor = db.query(SoundData.SQL_TABLE_NAME,
                columns,
                SoundData.SQL_ID + " = ?",
                selectionArgs,
                /* groupBy */ null,
                /* having */ null,
                /* orderBy */ null);
        cursor.moveToFirst();
        return cursor.isAfterLast() ? null : new Sound(new SoundData.Builder()
                .setId(cursor.getString(cursor.getColumnIndex(SoundData.SQL_ID)))
                .setUserId(cursor.getString(cursor.getColumnIndex(SoundData.SQL_USER_ID)))
                .setSoundFileUrl(cursor.getString(cursor.getColumnIndex(
                        SoundData.SQL_FILE_URL)))
                .setLocalFilePath(cursor.getString(cursor.getColumnIndex(
                        SoundData.SQL_LOCAL_FILE_PATH)))
                .setCreateDate(DateHelper.parse(cursor.getString(cursor.getColumnIndex(
                        SoundData.SQL_CREATE_DATE_TIME))))
                .build());
    }
}