package com.obnoxx.androidapp.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.obnoxx.androidapp.DateHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Data object representing a previously recorded sound.
 */
public class SoundData {
    private static final String TAG = "SoundData";

    // Local SQLite Database field names.
    public static final String SQL_TABLE_NAME = "Sound";
    public static final String SQL_ID = "id";
    public static final String SQL_USER_ID = "userId";
    public static final String SQL_FILE_URL = "fileUrl";
    public static final String SQL_LOCAL_FILE_PATH = "localFilePath";
    public static final String SQL_CREATE_DATE_TIME = "createDateTime";
    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + SQL_TABLE_NAME + " (" +
                    SQL_ID + " TEXT PRIMARY KEY, " +
                    SQL_USER_ID + " TEXT, " +
                    SQL_FILE_URL + " TEXT, " +
                    SQL_LOCAL_FILE_PATH + " TEXT, " +
                    SQL_CREATE_DATE_TIME + " TEXT);";
    public static final String SQL_CREATE_INDEX_USER_ID =
            "CREATE INDEX " + SQL_TABLE_NAME + "_" + SQL_USER_ID + "_index " +
                    "ON " + SQL_TABLE_NAME + "(" + SQL_USER_ID + ");";

    // Client/server JSON field names.
    private static final String JSON_ID = "id";
    private static final String JSON_USER_ID = "userId";
    private static final String JSON_FILE_URL = "soundFileUrl";
    private static final String JSON_CREATE_DATE_TIME = "createDateTime";

    private String mId;
    private String mUserId;
    private String mFileUrl;
    private String mLocalFilePath = null;
    private Date mCreateDate = null;

    private SoundData() {}

    public SoundData(String json) throws JSONException {
        this(new JSONObject(json));
    }

    public SoundData(JSONObject jsonObject) throws JSONException {
        mId = jsonObject.optString(JSON_ID, null);
        mUserId = jsonObject.optString(JSON_USER_ID, null);
        mFileUrl = jsonObject.optString(JSON_FILE_URL, null);
        mCreateDate = DateHelper.parse(jsonObject.optString(JSON_CREATE_DATE_TIME));
    }

    public void setLocalFilePath(String localFilePath) {
        mLocalFilePath = localFilePath;
    }

    public static class Builder {
        private String mId;
        private String mUserId;
        private String mFileUrl;
        private String mLocalFilePath;
        private Date mCreateDate;

        public Builder() {
        }

        public Builder(SoundData soundData) {
            this.mId = soundData.mId;
            this.mUserId = soundData.mUserId;
            this.mFileUrl = soundData.mFileUrl;
            this.mLocalFilePath = soundData.mLocalFilePath;
            this.mCreateDate = soundData.mCreateDate;
        }

        public Builder setId(String id) {
            this.mId = id;
            return this;
        }

        public Builder setUserId(String userId) {
            this.mUserId = userId;
            return this;
        }

        public Builder setSoundFileUrl(String soundFileUrl) {
            this.mFileUrl = soundFileUrl;
            return this;
        }

        public Builder setLocalFilePath(String localFilePath) {
            this.mLocalFilePath = localFilePath;
            return this;
        }

        public Builder setCreateDate(Date createDate) {
            this.mCreateDate = createDate;
            return this;
        }

        public SoundData build() {
            SoundData soundData = new SoundData();
            soundData.mId = mId;
            soundData.mUserId = mUserId;
            soundData.mFileUrl = mFileUrl;
            soundData.mLocalFilePath = mLocalFilePath;
            soundData.mCreateDate = mCreateDate;
            return soundData;
        }
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject o = new JSONObject();
        o.put(JSON_ID, mId);
        o.put(JSON_USER_ID, mUserId);
        o.put(JSON_FILE_URL, mFileUrl);
        o.put(JSON_CREATE_DATE_TIME, DateHelper.format(mCreateDate));

        // Delibrately not serializing LOCAL_FILE_PATH_STR, since this method
        // is used for server-delivery of data, and the server doesn't care
        // about where we locally store things.

        return o;
    }

    public ContentValues toValues() {
        ContentValues v = new ContentValues();
        v.put(SQL_ID, mId);
        v.put(SQL_USER_ID, mUserId);
        v.put(SQL_FILE_URL, mFileUrl);
        if (mLocalFilePath != null) {
            v.put(SQL_LOCAL_FILE_PATH, mLocalFilePath);
        }
        v.put(SQL_CREATE_DATE_TIME, DateHelper.format(mCreateDate));
        return v;
    }
    
    public String getId() {
        return mId;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getSoundFileUrl() {
        return mFileUrl;
    }

    public String getLocalFilePath() {
        return mLocalFilePath;
    }

    public Date getCreateDate() {
        return mCreateDate;
    }

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(SQL_CREATE_TABLE);
        database.execSQL(SQL_CREATE_INDEX_USER_ID);
    }
}
