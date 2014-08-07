package com.obnoxx.androidapp.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.obnoxx.androidapp.DateHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Data object representing a sound delivered to or by the current user.
 */
public class SoundDeliveryData {
    private static final String TAG = "SoundDeliveryData";

    // Local SQLite Database field names.
    public static final String SQL_TABLE_NAME = "SoundDelivery";
    public static final String SQL_ID = "id";
    public static final String SQL_SOUND_ID = "soundId";
    public static final String SQL_USER_ID = "userId";
    public static final String SQL_PHONE_NUMBER = "phoneNumber";
    public static final String SQL_RECIPIENT_USER_ID = "recipientUserId";
    public static final String SQL_DATE_TIME = "soundDeliveryDateTime";
    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + SQL_TABLE_NAME + " (" +
                    SQL_ID + " TEXT PRIMARY KEY, " +
                    SQL_SOUND_ID + " TEXT, " +
                    SQL_USER_ID + " TEXT, " +
                    SQL_PHONE_NUMBER + " TEXT, " +
                    SQL_RECIPIENT_USER_ID + " TEXT, " +
                    SQL_DATE_TIME + " TEXT);";
    public static final String SQL_CREATE_INDEX_USER_ID =
            "CREATE INDEX " + SQL_TABLE_NAME + "_" + SQL_USER_ID + "_index ON " +
                    SQL_TABLE_NAME + "(" + SQL_USER_ID + ");";
    public static final String SQL_CREATE_INDEX_RECIPIENT_USER_ID =
            "CREATE INDEX " + SQL_TABLE_NAME + "_" + SQL_RECIPIENT_USER_ID + "_index ON " +
                    SQL_TABLE_NAME + "(" + SQL_RECIPIENT_USER_ID + ");";
    public static final String SQL_CREATE_INDEX_SOUND_ID =
            "CREATE INDEX " + SQL_TABLE_NAME + "_" + SQL_SOUND_ID + "_index ON " +
                    SQL_TABLE_NAME + "(" + SQL_SOUND_ID + ");";

    // Client/server JSON field names.
    private static final String JSON_ID = "id";
    private static final String JSON_SOUND_ID = "soundId";
    private static final String JSON_USER_ID = "userId";
    private static final String JSON_PHONE_NUMBER = "phoneNumber";
    private static final String JSON_RECIPIENT_USER_ID = "recipientUserId";
    private static final String JSON_DELIVERY_DATE_TIME = "deliveryDateTime";

    private String mId;
    private String mSoundId;
    private String mUserId;
    private String mPhoneNumber;
    private String mRecipientUserId;
    private Date mDeliveryDate = null;

    private SoundDeliveryData() {}

    public SoundDeliveryData(String json) throws JSONException {
        this(new JSONObject(json));
    }

    public SoundDeliveryData(JSONObject jsonObject) throws JSONException {
        mId = jsonObject.optString(JSON_ID, null);
        mSoundId = jsonObject.optString(JSON_SOUND_ID, null);
        mUserId = jsonObject.optString(JSON_USER_ID, null);
        mPhoneNumber = jsonObject.optString(JSON_PHONE_NUMBER, null);
        mRecipientUserId = jsonObject.optString(JSON_RECIPIENT_USER_ID, null);
        mDeliveryDate = DateHelper.parse(jsonObject.optString(JSON_DELIVERY_DATE_TIME, null));
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

        public Builder(SoundDeliveryData soundDeliveryData) {
            this.mId = soundDeliveryData.mId;
            this.mSoundId = soundDeliveryData.mSoundId;
            this.mUserId = soundDeliveryData.mUserId;
            this.mPhoneNumber = soundDeliveryData.mPhoneNumber;
            this.mRecipientUserId = soundDeliveryData.mRecipientUserId;
            this.mDeliveryDate = soundDeliveryData.mDeliveryDate;
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

        public SoundDeliveryData build() {
            SoundDeliveryData soundDeliveryData = new SoundDeliveryData();
            soundDeliveryData.mId = mId;
            soundDeliveryData.mSoundId = mSoundId;
            soundDeliveryData.mUserId = mUserId;
            soundDeliveryData.mPhoneNumber = mPhoneNumber;
            soundDeliveryData.mRecipientUserId = mRecipientUserId;
            soundDeliveryData.mDeliveryDate = mDeliveryDate;
            return soundDeliveryData;
        }
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject o = new JSONObject();
        o.put(JSON_ID, mId);
        o.put(JSON_SOUND_ID, mSoundId);
        o.put(JSON_USER_ID, mUserId);
        o.put(JSON_PHONE_NUMBER, mPhoneNumber);
        o.put(JSON_RECIPIENT_USER_ID, mRecipientUserId);
        o.put(JSON_DELIVERY_DATE_TIME, DateHelper.format(mDeliveryDate));
        return o;
    }

    public ContentValues toValues() {
        ContentValues v = new ContentValues();
        v.put(SQL_ID, mId);
        v.put(SQL_SOUND_ID, mSoundId);
        v.put(SQL_USER_ID, mUserId);
        v.put(SQL_PHONE_NUMBER, mPhoneNumber);
        v.put(SQL_RECIPIENT_USER_ID, mRecipientUserId);
        v.put(SQL_DATE_TIME, DateHelper.format(mDeliveryDate));
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

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(SQL_CREATE_TABLE);
        database.execSQL(SQL_CREATE_INDEX_USER_ID);
        database.execSQL(SQL_CREATE_INDEX_RECIPIENT_USER_ID);
        database.execSQL(SQL_CREATE_INDEX_SOUND_ID);
    }
}
