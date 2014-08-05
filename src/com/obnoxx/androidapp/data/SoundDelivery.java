package com.obnoxx.androidapp.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;

import com.obnoxx.androidapp.DateHelper;

/**
 * Business logic for sound deliveries.  Deals with storing and retrieving
 * sound deliveries, while the actual serialization logic is in {@code
 * SoundDeliveryData}.
 */
public class SoundDelivery {
    private static final String TAG = "SoundDelivery";
    private static MediaPlayer mPlayer = null;
    private final SoundDeliveryData mSoundDeliveryData;

    public SoundDelivery(SoundDeliveryData soundDeliveryData) {
        mSoundDeliveryData = soundDeliveryData;
    }

    public SoundDeliveryData getData() {
        return mSoundDeliveryData;
    }

    public void save(Context context) {
        SQLiteDatabase db = new DatabaseHandler(context).getWritableDatabase();
        db.insertWithOnConflict(SoundDeliveryData.SQL_TABLE_NAME, null,
                mSoundDeliveryData.toValues(), SQLiteDatabase.CONFLICT_REPLACE);
    }

    public static SoundDelivery get(Context context, String soundDeliveryId) {
        SQLiteDatabase db = new DatabaseHandler(context).getReadableDatabase();
        String[] columns = {
                SoundDeliveryData.SQL_ID,
                SoundDeliveryData.SQL_SOUND_ID,
                SoundDeliveryData.SQL_USER_ID,
                SoundDeliveryData.SQL_PHONE_NUMBER,
                SoundDeliveryData.SQL_RECIPIENT_USER_ID,
                SoundDeliveryData.SQL_DATE_TIME
        };
        String[] selectionArgs = new String[] {
                soundDeliveryId
        };
        Cursor cursor = db.query(SoundDeliveryData.SQL_TABLE_NAME,
                columns,
                SoundDeliveryData.SQL_ID + " = ?",
                selectionArgs,
                /* groupBy */ null,
                /* having */ null,
                /* orderBy */ null);
        cursor.moveToFirst();
        return cursor.isAfterLast() ? null : new SoundDelivery(new SoundDeliveryData.Builder()
                .setId(cursor.getString(cursor.getColumnIndex(
                        SoundDeliveryData.SQL_ID)))
                .setSoundId(cursor.getString(cursor.getColumnIndex(
                        SoundDeliveryData.SQL_SOUND_ID)))
                .setUserId(cursor.getString(cursor.getColumnIndex(
                        SoundDeliveryData.SQL_USER_ID)))
                .setPhoneNumber(cursor.getString(cursor.getColumnIndex(
                        SoundDeliveryData.SQL_PHONE_NUMBER)))
                .setRecipientUserId(cursor.getString(cursor.getColumnIndex(
                        SoundDeliveryData.SQL_RECIPIENT_USER_ID)))
                .setDeliveryDate(DateHelper.parse(cursor.getString(cursor.getColumnIndex(
                        SoundDeliveryData.SQL_DATE_TIME))))
                .build());
    }
}
