package com.obnoxx.androidapp.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.obnoxx.androidapp.DateHelper;

/**
 * Business logic dealing with users.
 */
public class User {
    private static final String TAG = "User";

    private final UserData mUserData;

    public User(UserData userData) {
        mUserData = userData;
    }

    public UserData getData() {
        return mUserData;
    }

    public void save(Context context) {
        SQLiteDatabase db = new DatabaseHandler(context).getWritableDatabase();
        db.insertWithOnConflict(UserData.SQL_TABLE_NAME, null, mUserData.toValues(),
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    public static UserData get(Context context, String soundId) {
        SQLiteDatabase db = new DatabaseHandler(context).getReadableDatabase();
        String[] columns = {
                UserData.SQL_ID,
                UserData.SQL_NAME,
                UserData.SQL_EMAIL,
                UserData.SQL_PHONE_NUMBER,
                UserData.SQL_FACEBOOK_USER_ID,
                UserData.SQL_IMAGE_URL,
                UserData.SQL_LOCAL_FILE_PATH,
                UserData.SQL_CREATE_DATE_TIME,
        };
        String[] selectionArgs = new String[] {
                soundId
        };
        Cursor cursor = db.query(UserData.SQL_TABLE_NAME,
                columns,
                UserData.SQL_ID + " = ?",
                selectionArgs,
                /* groupBy */ null,
                /* having */ null,
                /* orderBy */ null);
        cursor.moveToFirst();
        return cursor.isAfterLast() ? null : new UserData.Builder()
                .setId(cursor.getString(cursor.getColumnIndex(UserData.SQL_ID)))
                .setName(cursor.getString(cursor.getColumnIndex(UserData.SQL_NAME)))
                .setEmail(cursor.getString(cursor.getColumnIndex(UserData.SQL_EMAIL)))
                .setPhoneNumber(cursor.getString(cursor.getColumnIndex(
                        UserData.SQL_PHONE_NUMBER)))
                .setFbUserId(cursor.getString(cursor.getColumnIndex(
                        UserData.SQL_FACEBOOK_USER_ID)))
                .setImageUrl(cursor.getString(cursor.getColumnIndex(
                        UserData.SQL_IMAGE_URL)))
                .setImageLocalFilePath(cursor.getString(cursor.getColumnIndex(
                        UserData.SQL_LOCAL_FILE_PATH)))
                .setCreateDate(DateHelper.parse(cursor.getString(cursor.getColumnIndex(
                        UserData.SQL_CREATE_DATE_TIME))))
                .build();
    }
}
