package com.obnoxx.androidapp;

import android.content.ContentValues;
import android.media.MediaPlayer;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Data object representing a previously recorded sound.
 */
public class Sound {
    private static final String TAG = "Sound";

    private static final String ID_STR = "id";
    private static final String USER_ID_STR = "userId";
    private static final String FILE_URL_STR = "soundFileUrl";
    private static final String LOCAL_FILE_PATH_STR = "localFilePath";
    private static final String CREATE_DATE_TIME_STR = "createDateTime";
    public static final DateFormat DATE_TIME_FORMATTER =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static MediaPlayer mPlayer = null;

    private String mId;
    private String mUserId;
    private String mFileUrl;
    private String mLocalFilePath;
    private Date mCreateDate = null;

    private Sound() {}

    public Sound(String json) throws JSONException {
        this(new JSONObject(json));
    }

    public Sound(JSONObject jsonObject) throws JSONException {
        mId = jsonObject.optString(ID_STR);
        mUserId = jsonObject.optString(USER_ID_STR);
        mFileUrl = jsonObject.optString(FILE_URL_STR);
        mLocalFilePath = jsonObject.optString(LOCAL_FILE_PATH_STR);

        try {
            mCreateDate = DATE_TIME_FORMATTER.parse(jsonObject.optString(CREATE_DATE_TIME_STR));
        } catch (ParseException e) {
            Log.w(TAG, "Could not parse date: " + jsonObject.optString(CREATE_DATE_TIME_STR), e);
        }
    }

    public static class Builder {
        private String mId;
        private String mUserId;
        private String mFileUrl;
        private String mLocalFilePath;
        private Date mCreateDate;

        public Builder() {
        }

        public Builder(Sound sound) {
            this.mId = sound.mId;
            this.mUserId = sound.mUserId;
            this.mFileUrl = sound.mFileUrl;
            this.mLocalFilePath = sound.mLocalFilePath;
            this.mCreateDate = sound.mCreateDate;
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

        public Sound build() {
            Sound sound = new Sound();
            sound.mId = mId;
            sound.mUserId = mUserId;
            sound.mFileUrl = mFileUrl;
            sound.mLocalFilePath = mLocalFilePath;
            sound.mCreateDate = mCreateDate;
            return sound;
        }
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject o = new JSONObject();
        o.put(ID_STR, mId);
        o.put(USER_ID_STR, mUserId);
        o.put(FILE_URL_STR, mFileUrl);
        o.put(CREATE_DATE_TIME_STR, DATE_TIME_FORMATTER.format(mCreateDate));

        // Delibrately not serializing LOCAL_FILE_PATH_STR, since this method
        // is used for server-delivery of data, and the server doesn't care
        // about where we locally store things.

        return o;
    }

    public ContentValues toValues() {
        ContentValues v = new ContentValues();
        v.put(DatabaseHandler.SOUND_ID, mId);
        v.put(DatabaseHandler.SOUND_USER_ID, mUserId);
        v.put(DatabaseHandler.SOUND_FILE_URL, mFileUrl);
        if (mLocalFilePath != null) {
            v.put(DatabaseHandler.SOUND_LOCAL_FILE_PATH, mLocalFilePath);
        }
        v.put(DatabaseHandler.SOUND_CREATE_DATE_TIME, DATE_TIME_FORMATTER.format(mCreateDate));
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

    // TODO(jonemerson): This method no longer belongs here.  There should just be
    // a sound playback manager somewhere that takes Files to play.  This class
    // should solely be responsible for representing data in memory that comes to
    // or goes to the server or the Sqlite database.
    public void play() {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        } else {
            mPlayer.reset();
        }

        try {
            mPlayer.setDataSource(mLocalFilePath);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
