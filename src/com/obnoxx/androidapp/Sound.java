package com.obnoxx.androidapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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

    public static final String ID_STR = "id";
    public static final String USER_ID_STR = "userId";
    public static final String FILE_URL_STR = "soundFileUrl";
    public static final String LOCAL_FILE_PATH_STR = "localFilePath";
    public static final String CREATE_DATE_TIME_STR = "createDateTime";
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
        mId = jsonObject.optString(ID_STR, null);
        mUserId = jsonObject.optString(USER_ID_STR, null);
        mFileUrl = jsonObject.optString(FILE_URL_STR, null);
        mLocalFilePath = jsonObject.optString(LOCAL_FILE_PATH_STR, null);

        try {
            mCreateDate = DATE_TIME_FORMATTER.parse(jsonObject.optString(CREATE_DATE_TIME_STR));
        } catch (ParseException e) {
            Log.w(TAG, "Could not parse date: " + jsonObject.optString(CREATE_DATE_TIME_STR), e);
        }
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

    // TODO(jonemerson): Does this method belong here?  What is our object representation
    // of Sounds / Deliveries / Users?  Do we have a separation of our data representations
    // and business logic?  Probably we do want such a separation... make it happen.
    public void play() {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        } else {
            mPlayer.reset();
        }

        try {
            try {
                mPlayer.setDataSource(mLocalFilePath);
            } catch (IOException e) {
                Log.e(TAG, "Could not play sound, it is corrupted.  File size = " +
                        new File(mLocalFilePath).length());
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
        db.insertWithOnConflict(DatabaseHandler.SOUND_TABLE_NAME, null, this.toValues(),
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    public static Sound get(Context context, String soundId) {
        SQLiteDatabase db = new DatabaseHandler(context).getReadableDatabase();
        String[] columns = {
                DatabaseHandler.SOUND_ID,
                DatabaseHandler.SOUND_USER_ID,
                DatabaseHandler.SOUND_FILE_URL,
                DatabaseHandler.SOUND_LOCAL_FILE_PATH,
                DatabaseHandler.SOUND_CREATE_DATE_TIME,
        };
        String[] selectionArgs = new String[] {
                soundId
        };
        Cursor cursor = db.query(DatabaseHandler.SOUND_TABLE_NAME,
                columns,
                DatabaseHandler.SOUND_ID + " = ?",
                selectionArgs,
                /* groupBy */ null,
                /* having */ null,
                /* orderBy */ null);
        cursor.moveToFirst();
        return cursor.isAfterLast() ? null : new Sound.Builder()
                .setId(cursor.getString(cursor.getColumnIndex(DatabaseHandler.SOUND_ID)))
                .setUserId(cursor.getString(cursor.getColumnIndex(DatabaseHandler.SOUND_USER_ID)))
                .setSoundFileUrl(cursor.getString(cursor.getColumnIndex(
                        DatabaseHandler.SOUND_FILE_URL)))
                .setLocalFilePath(cursor.getString(cursor.getColumnIndex(
                        DatabaseHandler.SOUND_LOCAL_FILE_PATH)))
                .setCreateDate(createDate(cursor.getString(cursor.getColumnIndex(
                        DatabaseHandler.SOUND_CREATE_DATE_TIME))))
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
