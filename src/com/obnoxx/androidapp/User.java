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
 * Data object representing a user, who may or may not be the current user.
 * Ultimately the client will have a database-backed cache of all the Users
 * he has shared sounds with, and those Users will be represented in memory
 * with this object.
 */
public class User {
    private static final String TAG = "User";

    private static final String ID_STR = "id";
    private static final String EMAIL_STR = "email";
    private static final String NAME_STR = "name";
    private static final String PHONE_NUMBER_STR = "phoneNumber";
    private static final String FACEBOOK_USER_ID_STR = "fbUserId";
    private static final String IMAGE_URL_STR = "imageUrl";
    private static final String IMAGE_LOCAL_FILE_PATH_STR = "imageLocalFilePath";
    private static final String CREATE_DATE_TIME_STR = "createDateTime";
    public static final DateFormat DATE_TIME_FORMATTER =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String mId;
    private String mEmail;
    private String mName;
    private String mPhoneNumber;
    private String mFacebookUserId;
    private String mImageUrl;
    private String mImageLocalFilePath;
    private Date mCreateDate = null;

    private User() {}

    public User(String json) throws JSONException {
        this(new JSONObject(json));
    }

    public User(JSONObject jsonObject) throws JSONException {
        mId = jsonObject.optString(ID_STR, null);
        mEmail = jsonObject.optString(EMAIL_STR, null);
        mName = jsonObject.optString(NAME_STR, null);
        mPhoneNumber = jsonObject.optString(PHONE_NUMBER_STR, null);
        mFacebookUserId = jsonObject.optString(FACEBOOK_USER_ID_STR, null);
        mImageUrl = jsonObject.optString(IMAGE_URL_STR, null);

        try {
            mCreateDate = DATE_TIME_FORMATTER.parse(
                    jsonObject.optString(CREATE_DATE_TIME_STR, null));
        } catch (ParseException e) {
            Log.w(TAG, "Could not parse date: " + jsonObject.optString(CREATE_DATE_TIME_STR), e);
        }
    }

    public static class Builder {
        private String mId;
        private String mEmail;
        private String mName;
        private String mPhoneNumber;
        private String mFacebookUserId;
        private String mImageUrl;
        private String mImageLocalFilePath;
        private Date mCreateDate;

        public Builder() {
        }

        public Builder(User user) {
            this.mId = user.mId;
            this.mEmail = user.mEmail;
            this.mName = user.mName;
            this.mPhoneNumber = user.mPhoneNumber;
            this.mFacebookUserId = user.mFacebookUserId;
            this.mImageUrl = user.mImageUrl;
            this.mImageLocalFilePath = user.mImageLocalFilePath;
            this.mCreateDate = user.mCreateDate;
        }

        public Builder setId(String id) {
            this.mId = id;
            return this;
        }

        public Builder setEmail(String email) {
            this.mEmail = email;
            return this;
        }

        public Builder setName(String name) {
            this.mName = name;
            return this;
        }

        public Builder setPhoneNumber(String phoneNumber) {
            this.mPhoneNumber = phoneNumber;
            return this;
        }

        public Builder setFbUserId(String facebookUserId) {
            this.mFacebookUserId = facebookUserId;
            return this;
        }

        public Builder setImageUrl(String imageUrl) {
            this.mImageUrl = imageUrl;
            return this;
        }

        public Builder setImageLocalFilePath(String imageLocalFilePath) {
            this.mImageLocalFilePath = imageLocalFilePath;
            return this;
        }

        public Builder setCreateDate(Date createDate) {
            this.mCreateDate = createDate;
            return this;
        }

        public User build() {
            User user = new User();
            user.mId = mId;
            user.mEmail = mEmail;
            user.mName = mName;
            user.mPhoneNumber = mPhoneNumber;
            user.mFacebookUserId = mFacebookUserId;
            user.mImageUrl = mImageUrl;
            user.mImageLocalFilePath = mImageLocalFilePath;
            user.mCreateDate = mCreateDate;
            return user;
        }
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject o = new JSONObject();
        o.put(ID_STR, mId);
        o.put(NAME_STR, mName);
        o.put(PHONE_NUMBER_STR, mPhoneNumber);
        o.put(CREATE_DATE_TIME_STR, DATE_TIME_FORMATTER.format(mCreateDate));

        if (mEmail != null) {
            o.put(EMAIL_STR, mEmail);
        }

        if (mImageUrl != null) {
            o.put(IMAGE_URL_STR, mImageUrl);
        }

        if (mFacebookUserId != null) {
            o.put(FACEBOOK_USER_ID_STR, mFacebookUserId);
        }

        // Deliberately not serializing mImageLocalFilePath, since the server
        // doesn't need to know about our local caching strategy.

        return o;
    }

    public ContentValues toValues() {
        ContentValues v = new ContentValues();
        v.put(DatabaseHandler.USER_ID, mId);
        v.put(DatabaseHandler.USER_NAME, mName);
        v.put(DatabaseHandler.USER_PHONE_NUMBER, mPhoneNumber);
        v.put(DatabaseHandler.USER_CREATE_DATE_TIME, DATE_TIME_FORMATTER.format(mCreateDate));

        if (mEmail != null) {
            v.put(DatabaseHandler.USER_EMAIL, mEmail);
        }

        if (mImageUrl != null) {
            v.put(DatabaseHandler.USER_IMAGE_FILENAME, mImageUrl);
        }

        if (mFacebookUserId != null) {
            v.put(DatabaseHandler.USER_FACEBOOK_USER_ID, mFacebookUserId);
        }

        if (mImageLocalFilePath != null) {
            v.put(DatabaseHandler.USER_IMAGE_LOCAL_FILE_PATH, mImageLocalFilePath);
        }

        return v;
    }

    public String getId() {
        return mId;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getName() {
        return mName;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public String getFacebookUserId() {
        return mFacebookUserId;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getImageLocalFilePath() {
        return mImageLocalFilePath;
    }

    public Date getCreateDate() {
        return mCreateDate;
    }

    public void save(Context context) {
        SQLiteDatabase db = new DatabaseHandler(context).getWritableDatabase();
        db.insertWithOnConflict(DatabaseHandler.USER_TABLE_NAME, null, this.toValues(),
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    public static User get(Context context, String soundId) {
        SQLiteDatabase db = new DatabaseHandler(context).getReadableDatabase();
        String[] columns = {
                DatabaseHandler.USER_ID,
                DatabaseHandler.USER_NAME,
                DatabaseHandler.USER_EMAIL,
                DatabaseHandler.USER_PHONE_NUMBER,
                DatabaseHandler.USER_FACEBOOK_USER_ID,
                DatabaseHandler.USER_IMAGE_FILENAME,
                DatabaseHandler.USER_IMAGE_LOCAL_FILE_PATH,
                DatabaseHandler.USER_CREATE_DATE_TIME,
        };
        String[] selectionArgs = new String[] {
                soundId
        };
        Cursor cursor = db.query(DatabaseHandler.USER_TABLE_NAME,
                columns,
                DatabaseHandler.USER_ID + " = ?",
                selectionArgs,
                /* groupBy */ null,
                /* having */ null,
                /* orderBy */ null);
        cursor.moveToFirst();
        return cursor.isAfterLast() ? null : new User.Builder()
                .setId(cursor.getString(cursor.getColumnIndex(DatabaseHandler.SOUND_ID)))
                .setName(cursor.getString(cursor.getColumnIndex(DatabaseHandler.USER_NAME)))
                .setEmail(cursor.getString(cursor.getColumnIndex(DatabaseHandler.USER_EMAIL)))
                .setPhoneNumber(cursor.getString(cursor.getColumnIndex(
                        DatabaseHandler.USER_PHONE_NUMBER)))
                .setFbUserId(cursor.getString(cursor.getColumnIndex(
                        DatabaseHandler.USER_FACEBOOK_USER_ID)))
                .setImageUrl(cursor.getString(cursor.getColumnIndex(
                        DatabaseHandler.USER_IMAGE_FILENAME)))
                .setImageLocalFilePath(cursor.getString(cursor.getColumnIndex(
                        DatabaseHandler.USER_IMAGE_LOCAL_FILE_PATH)))
                .setCreateDate(createDate(cursor.getString(
                        cursor.getColumnIndex(DatabaseHandler.SOUND_CREATE_DATE_TIME))))
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
