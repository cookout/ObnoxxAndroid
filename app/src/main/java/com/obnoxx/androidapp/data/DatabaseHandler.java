package com.obnoxx.androidapp.data;

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
    private static final int DATABASE_VERSION = 11;

    // Database definitions.
    public static final String CURSOR_ID = "_id";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        ContactData.onCreate(sqLiteDatabase);
        SoundData.onCreate(sqLiteDatabase);
        SoundDeliveryData.onCreate(sqLiteDatabase);
        UserData.onCreate(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " +
                newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + UserData.SQL_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SoundData.SQL_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SoundDeliveryData.SQL_TABLE_NAME);
        onCreate(db);
    }
}