package com.obnoxx.androidapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Data object representing a sound delivered to or by the current user.
 */
public class SoundDelivery {
    private static final String TAG = "SoundDelivery";

    private static final String ID_STR = "id";
    private static final String SOUND_ID_STR = "soundId";
    private static final String USER_ID_STR = "userId";
    private static final String PHONE_NUMBER_STR = "phoneNumber";
    private static final String RECIPIENT_USER_ID_STR = "recipientUserId";
    private static final String DELIVERY_DATE_TIME_STR = "deliveryDateTime";

    public static final DateFormat DATE_TIME_FORMATTER =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String mId;
    private String mSoundId;
    private String mUserId;
    private String mPhoneNumber;
    private String mRecipientUserId;
    private Date mDeliveryDate = null;

    private SoundDelivery() {}

    public SoundDelivery(String json) throws JSONException {
        this(new JSONObject(json));
    }

    public SoundDelivery(JSONObject jsonObject) throws JSONException {
        mId = jsonObject.optString(ID_STR, null);
        mSoundId = jsonObject.optString(SOUND_ID_STR, null);
        mUserId = jsonObject.optString(USER_ID_STR, null);
        mPhoneNumber = jsonObject.optString(PHONE_NUMBER_STR, null);
        mRecipientUserId = jsonObject.optString(RECIPIENT_USER_ID_STR, null);

        try {
            mDeliveryDate = DATE_TIME_FORMATTER.parse(
                    jsonObject.optString(DELIVERY_DATE_TIME_STR, null));
        } catch (ParseException e) {
            Log.w(TAG, "Could not parse date: " +
                    jsonObject.optString(DELIVERY_DATE_TIME_STR), e);
        }
    }

    public static class Builder {
        private String mId;
        private String mSoundId;
        private String mUserId;
        private String mPhoneNumber;
        private String mRecipientUserId;
        private Date mDeliveryDate = null;

        public Builder() {
        }

        public Builder(SoundDelivery soundDelivery) {
            this.mId = soundDelivery.mId;
            this.mSoundId = soundDelivery.mSoundId;
            this.mUserId = soundDelivery.mUserId;
            this.mPhoneNumber = soundDelivery.mPhoneNumber;
            this.mRecipientUserId = soundDelivery.mRecipientUserId;
            this.mDeliveryDate = soundDelivery.mDeliveryDate;
        }

        public Builder setId(String id) {
            this.mId = id;
            return this;
        }

        public Builder setSoundId(String soundId) {
            this.mSoundId = soundId;
            return this;
        }

        public Builder setUserId(String UserId) {
            this.mUserId = UserId;
            return this;
        }

        public Builder setPhoneNumber(String phoneNumber) {
            this.mPhoneNumber = phoneNumber;
            return this;
        }

        public Builder setRecipientUserId(String recipientUserId) {
            this.mRecipientUserId = recipientUserId;
            return this;
        }

        public Builder setDeliveryDate(Date deliveryDate) {
            this.mDeliveryDate = deliveryDate;
            return this;
        }

        public SoundDelivery build() {
            SoundDelivery soundDelivery = new SoundDelivery();
            soundDelivery.mId = mId;
            soundDelivery.mSoundId = mSoundId;
            soundDelivery.mUserId = mUserId;
            soundDelivery.mPhoneNumber = mPhoneNumber;
            soundDelivery.mRecipientUserId = mRecipientUserId;
            soundDelivery.mDeliveryDate = mDeliveryDate;
            return soundDelivery;
        }
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject o = new JSONObject();
        o.put(ID_STR, mId);
        o.put(SOUND_ID_STR, mSoundId);
        o.put(USER_ID_STR, mUserId);
        o.put(PHONE_NUMBER_STR, mPhoneNumber);
        o.put(RECIPIENT_USER_ID_STR, mRecipientUserId);
        o.put(DELIVERY_DATE_TIME_STR, DATE_TIME_FORMATTER.format(mDeliveryDate));
        return o;
    }

    public ContentValues toValues() {
        ContentValues v = new ContentValues();
        v.put(DatabaseHandler.SOUND_DELIVERY_ID, mId);
        v.put(DatabaseHandler.SOUND_DELIVERY_SOUND_ID, mSoundId);
        v.put(DatabaseHandler.SOUND_DELIVERY_USER_ID, mUserId);
        v.put(DatabaseHandler.SOUND_DELIVERY_PHONE_NUMBER, mPhoneNumber);
        v.put(DatabaseHandler.SOUND_DELIVERY_RECIPIENT_USER_ID, mRecipientUserId);
        v.put(DatabaseHandler.SOUND_DELIVERY_DATE_TIME, DATE_TIME_FORMATTER.format(mDeliveryDate));
        return v;
    }

    public String getId() {
        return mId;
    }

    public String getSoundId() {
        return mSoundId;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public String getRecipientUserId() {
        return mRecipientUserId;
    }

    public Date getDeliveryDate() {
        return mDeliveryDate;
    }

    public void save(Context context) {
        SQLiteDatabase db = new DatabaseHandler(context).getWritableDatabase();
        db.insertWithOnConflict(DatabaseHandler.SOUND_DELIVERY_TABLE_NAME, null, this.toValues(),
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    public static SoundDelivery get(Context context, String soundDeliveryId) {
        SQLiteDatabase db = new DatabaseHandler(context).getReadableDatabase();
        String[] columns = {
                DatabaseHandler.SOUND_DELIVERY_ID,
                DatabaseHandler.SOUND_DELIVERY_SOUND_ID,
                DatabaseHandler.SOUND_DELIVERY_USER_ID,
                DatabaseHandler.SOUND_DELIVERY_PHONE_NUMBER,
                DatabaseHandler.SOUND_DELIVERY_RECIPIENT_USER_ID,
                DatabaseHandler.SOUND_DELIVERY_DATE_TIME
        };
        String[] selectionArgs = new String[] {
                soundDeliveryId
        };
        Cursor cursor = db.query(DatabaseHandler.SOUND_DELIVERY_TABLE_NAME,
                columns,
                DatabaseHandler.SOUND_DELIVERY_ID + " = ?",
                selectionArgs,
                /* groupBy */ null,
                /* having */ null,
                /* orderBy */ null);
        cursor.moveToFirst();
        return cursor.isAfterLast() ? null : new SoundDelivery.Builder()
                .setId(cursor.getString(cursor.getColumnIndex(DatabaseHandler.SOUND_ID)))
                .setSoundId(cursor.getString(cursor.getColumnIndex(
                        DatabaseHandler.SOUND_DELIVERY_SOUND_ID)))
                .setUserId(cursor.getString(cursor.getColumnIndex(
                        DatabaseHandler.SOUND_USER_ID)))
                .setPhoneNumber(cursor.getString(cursor.getColumnIndex(
                        DatabaseHandler.SOUND_DELIVERY_PHONE_NUMBER)))
                .setRecipientUserId(cursor.getString(
                        cursor.getColumnIndex(DatabaseHandler.SOUND_DELIVERY_RECIPIENT_USER_ID)))
                .setDeliveryDate(createDate(cursor.getString(
                        cursor.getColumnIndex(DatabaseHandler.SOUND_DELIVERY_DATE_TIME))))
                .build();
    }

    /**
     * Parses a date.
     * TODO(jonemerson): Find a place to put some global Date handling utilities.
     */
    private static Date createDate(String dateStr) {
        Date deliveryDate = null;
        try {
            return SoundDelivery.DATE_TIME_FORMATTER.parse(dateStr);
        } catch (ParseException e) {
            Log.e(TAG, "Could not parse date: " + dateStr, e);
            return new Date();
        }
    }
}
