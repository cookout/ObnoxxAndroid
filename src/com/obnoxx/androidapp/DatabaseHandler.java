package com.obnoxx.androidapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Handles the creation of the database.  We probably want to refactor this and
 * the respective data classes (Sound, SoundDelivery, User) in the immediate
 * future, but for now, this gets us half way there.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHandler";
    private static final String DATABASE_NAME = "obnoxx_database";
    private static final int DATABASE_VERSION = 8;

    // Database definitions.
    public static final String USER_TABLE_NAME = "User";
    public static final String USER_ID = "id";
    public static final String USER_EMAIL = "email";
    public static final String USER_NAME = "name";
    public static final String USER_PHONE_NUMBER = "phoneNumber";
    public static final String USER_FACEBOOK_USER_ID = "fbUserId";
    public static final String USER_IMAGE_FILENAME = "imageFilename";
    public static final String USER_IMAGE_LOCAL_FILE_PATH = "imageLocalFilePath";
    public static final String USER_CREATE_DATE_TIME = "createDateTime";
    public static final String CREATE_TABLE_USER =
            "CREATE TABLE " + USER_TABLE_NAME + " (" +
                    USER_ID + " TEXT PRIMARY KEY, " +
                    USER_EMAIL + " TEXT, " +
                    USER_NAME + " TEXT, " +
                    USER_PHONE_NUMBER + " TEXT, " +
                    USER_FACEBOOK_USER_ID + " TEXT, " +
                    USER_IMAGE_FILENAME + " TEXT, " +
                    USER_IMAGE_LOCAL_FILE_PATH + " TEXT, " +
                    USER_CREATE_DATE_TIME + " TEXT);";
    public static final String CREATE_INDEX_USER_PHONE_NUMBER =
            "CREATE INDEX " + USER_TABLE_NAME + "_" + USER_PHONE_NUMBER + "_index " +
                    "ON " + USER_TABLE_NAME + "(" + USER_PHONE_NUMBER + ");";
    public static final String CREATE_INDEX_USER_FACEBOOK_USER_ID =
            "CREATE INDEX " + USER_TABLE_NAME + "_" + USER_FACEBOOK_USER_ID + "_index " +
                    "ON " + USER_TABLE_NAME + "(" + USER_FACEBOOK_USER_ID + ");";

    public static final String SOUND_TABLE_NAME = "Sound";
    public static final String SOUND_ID = "id";
    public static final String SOUND_USER_ID = "userId";
    public static final String SOUND_FILE_URL = "fileUrl";
    public static final String SOUND_LOCAL_FILE_PATH = "localFilePath";
    public static final String SOUND_CREATE_DATE_TIME = "createDateTime";
    public static final String CREATE_TABLE_SOUND =
            "CREATE TABLE " + SOUND_TABLE_NAME + " (" +
                    SOUND_ID + " TEXT PRIMARY KEY, " +
                    SOUND_USER_ID + " TEXT, " +
                    SOUND_FILE_URL + " TEXT, " +
                    SOUND_LOCAL_FILE_PATH + " TEXT, " +
                    SOUND_CREATE_DATE_TIME + " TEXT);";
    public static final String CREATE_INDEX_SOUND_USER_ID =
            "CREATE INDEX " + SOUND_TABLE_NAME + "_" + SOUND_USER_ID + "_index " +
                    "ON " + SOUND_TABLE_NAME + "(" + SOUND_USER_ID + ");";

    public static final String SOUND_DELIVERY_TABLE_NAME = "SoundDelivery";
    public static final String SOUND_DELIVERY_ID = "id";
    public static final String SOUND_DELIVERY_SOUND_ID = "soundId";
    public static final String SOUND_DELIVERY_USER_ID = "userId";
    public static final String SOUND_DELIVERY_PHONE_NUMBER = "phoneNumber";
    public static final String SOUND_DELIVERY_RECIPIENT_USER_ID = "recipientUserId";
    public static final String SOUND_DELIVERY_DATE_TIME = "soundDeliveryDateTime";
    public static final String CREATE_TABLE_SOUND_DELIVERY =
            "CREATE TABLE " + SOUND_DELIVERY_TABLE_NAME + " (" +
                    SOUND_DELIVERY_ID + " TEXT PRIMARY KEY, " +
                    SOUND_DELIVERY_SOUND_ID + " TEXT, " +
                    SOUND_DELIVERY_USER_ID + " TEXT, " +
                    SOUND_DELIVERY_PHONE_NUMBER + " TEXT, " +
                    SOUND_DELIVERY_RECIPIENT_USER_ID + " TEXT, " +
                    SOUND_DELIVERY_DATE_TIME + " TEXT);";
    public static final String CREATE_INDEX_SOUND_DELIVERY_USER_ID =
            "CREATE INDEX " + SOUND_DELIVERY_TABLE_NAME + "_" + SOUND_DELIVERY_USER_ID + "_index " +
                    "ON " + SOUND_DELIVERY_TABLE_NAME + "(" + SOUND_DELIVERY_USER_ID + ");";
    public static final String CREATE_INDEX_SOUND_DELIVERY_RECIPIENT_USER_ID =
            "CREATE INDEX " + SOUND_DELIVERY_TABLE_NAME + "_" + SOUND_DELIVERY_RECIPIENT_USER_ID +
                    "_index " + "ON " + SOUND_DELIVERY_TABLE_NAME + "(" +
                    SOUND_DELIVERY_RECIPIENT_USER_ID + ");";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_USER);
        database.execSQL(CREATE_INDEX_USER_PHONE_NUMBER);
        database.execSQL(CREATE_INDEX_USER_FACEBOOK_USER_ID);
        database.execSQL(CREATE_TABLE_SOUND);
        database.execSQL(CREATE_INDEX_SOUND_USER_ID);
        database.execSQL(CREATE_TABLE_SOUND_DELIVERY);
        database.execSQL(CREATE_INDEX_SOUND_DELIVERY_USER_ID);
        database.execSQL(CREATE_INDEX_SOUND_DELIVERY_RECIPIENT_USER_ID);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " +
                newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SOUND_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SOUND_DELIVERY_TABLE_NAME);
        onCreate(db);
    }
}