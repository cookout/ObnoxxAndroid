package com.obnoxx.androidapp.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.obnoxx.androidapp.DateHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Data object representing a user, who may or may not be the current user.
 */
public class UserData {
    private static final String TAG = "UserData";

    // Local SQLite Database field names.
    public static final String SQL_TABLE_NAME = "User";
    public static final String SQL_ID = "id";
    public static final String SQL_EMAIL = "email";
    public static final String SQL_NAME = "name";
    public static final String SQL_PHONE_NUMBER = "phoneNumber";
    public static final String SQL_FACEBOOK_USER_ID = "fbUserId";
    public static final String SQL_IMAGE_URL = "imageUrl";
    public static final String SQL_LOCAL_FILE_PATH = "localFilePath";
    public static final String SQL_CREATE_DATE_TIME = "createDateTime";
    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + SQL_TABLE_NAME + " (" +
                    SQL_ID + " TEXT PRIMARY KEY, " +
                    SQL_EMAIL + " TEXT, " +
                    SQL_NAME + " TEXT, " +
                    SQL_PHONE_NUMBER + " TEXT, " +
                    SQL_FACEBOOK_USER_ID + " TEXT, " +
                    SQL_IMAGE_URL + " TEXT, " +
                    SQL_LOCAL_FILE_PATH + " TEXT, " +
                    SQL_CREATE_DATE_TIME + " TEXT);";
    public static final String SQL_CREATE_INDEX_PHONE_NUMBER =
            "CREATE INDEX " + SQL_TABLE_NAME + "_" + SQL_PHONE_NUMBER + "_index " +
                    "ON " + SQL_TABLE_NAME + "(" + SQL_PHONE_NUMBER + ");";
    public static final String SQL_CREATE_INDEX_FACEBOOK_USER_ID =
            "CREATE INDEX " + SQL_TABLE_NAME + "_" + SQL_FACEBOOK_USER_ID + "_index " +
                    "ON " + SQL_TABLE_NAME + "(" + SQL_FACEBOOK_USER_ID + ");";

    // Client/server JSON field names.
    private static final String JSON_ID = "id";
    private static final String JSON_EMAIL = "email";
    private static final String JSON_NAME = "name";
    private static final String JSON_PHONE_NUMBER = "phoneNumber";
    private static final String JSON_FACEBOOK_USER_ID = "fbUserId";
    private static final String JSON_IMAGE_URL = "imageUrl";
    private static final String JSON_CREATE_DATE_TIME = "createDateTime";

    private String mId;
    private String mEmail;
    private String mName;
    private String mPhoneNumber;
    private String mFacebookUserId;
    private String mImageUrl;
    private String mImageLocalFilePath;
    private Date mCreateDate = null;

    private UserData() {}

    public UserData(String json) throws JSONException {
        this(new JSONObject(json));
    }

    public UserData(JSONObject jsonObject) throws JSONException {
        mId = jsonObject.optString(JSON_ID, null);
        mEmail = jsonObject.optString(JSON_EMAIL, null);
        mName = jsonObject.optString(JSON_NAME, null);
        mPhoneNumber = jsonObject.optString(JSON_PHONE_NUMBER, null);
        mFacebookUserId = jsonObject.optString(JSON_FACEBOOK_USER_ID, null);
        mImageUrl = jsonObject.optString(JSON_IMAGE_URL, null);
        mCreateDate = DateHelper.parse(jsonObject.optString(JSON_CREATE_DATE_TIME, null));
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

        public Builder(UserData userData) {
            this.mId = userData.mId;
            this.mEmail = userData.mEmail;
            this.mName = userData.mName;
            this.mPhoneNumber = userData.mPhoneNumber;
            this.mFacebookUserId = userData.mFacebookUserId;
            this.mImageUrl = userData.mImageUrl;
            this.mImageLocalFilePath = userData.mImageLocalFilePath;
            this.mCreateDate = userData.mCreateDate;
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

        public UserData build() {
            UserData userData = new UserData();
            userData.mId = mId;
            userData.mEmail = mEmail;
            userData.mName = mName;
            userData.mPhoneNumber = mPhoneNumber;
            userData.mFacebookUserId = mFacebookUserId;
            userData.mImageUrl = mImageUrl;
            userData.mImageLocalFilePath = mImageLocalFilePath;
            userData.mCreateDate = mCreateDate;
            return userData;
        }
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject o = new JSONObject();
        o.put(JSON_ID, mId);
        o.put(JSON_NAME, mName);
        o.put(JSON_PHONE_NUMBER, mPhoneNumber);
        o.put(JSON_CREATE_DATE_TIME, DateHelper.format(mCreateDate));

        if (mEmail != null) {
            o.put(JSON_EMAIL, mEmail);
        }

        if (mImageUrl != null) {
            o.put(JSON_IMAGE_URL, mImageUrl);
        }

        if (mFacebookUserId != null) {
            o.put(JSON_FACEBOOK_USER_ID, mFacebookUserId);
        }

        // Deliberately not serializing mImageLocalFilePath, since the server
        // doesn't need to know about our local caching strategy.

        return o;
    }

    public ContentValues toValues() {
        ContentValues v = new ContentValues();
        v.put(SQL_ID, mId);
        v.put(SQL_NAME, mName);
        v.put(SQL_PHONE_NUMBER, mPhoneNumber);
        v.put(SQL_CREATE_DATE_TIME, DateHelper.format(mCreateDate));

        if (mEmail != null) {
            v.put(SQL_EMAIL, mEmail);
        }

        if (mImageUrl != null) {
            v.put(SQL_IMAGE_URL, mImageUrl);
        }

        if (mFacebookUserId != null) {
            v.put(SQL_FACEBOOK_USER_ID, mFacebookUserId);
        }

        if (mImageLocalFilePath != null) {
            v.put(SQL_LOCAL_FILE_PATH, mImageLocalFilePath);
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

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(SQL_CREATE_TABLE);
        database.execSQL(SQL_CREATE_INDEX_PHONE_NUMBER);
        database.execSQL(SQL_CREATE_INDEX_FACEBOOK_USER_ID);
    }
}
