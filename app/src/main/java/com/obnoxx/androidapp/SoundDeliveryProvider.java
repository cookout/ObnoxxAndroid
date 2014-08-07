package com.obnoxx.androidapp;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.obnoxx.androidapp.data.DatabaseHandler;
import com.obnoxx.androidapp.data.SoundData;
import com.obnoxx.androidapp.data.SoundDeliveryData;

/**
 * A content provider for sound deliveries and their underlying sounds.
 *
 * TODO(jonemerson): Should we have separate content providers for each thing we want to query?
 * Because right now this class combines sound list queries with individual sound queries, which
 * may not be the best idea.
 */
public class SoundDeliveryProvider extends ContentProvider {
    private static final String TAG = "SoundDeliveryProvider";
    private static final String AUTHORITY = SoundDeliveryProvider.class.getCanonicalName();

    public static final int DELIVERIES_FOR_USER_ID = 100;
    public static final int DELIVERY_BY_ID = 110;
    private static final String DELIVERIES_FOR_PATH = "deliveries/for";
    private static final String DELIVERY_PATH = "delivery";
    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, DELIVERIES_FOR_PATH + "/*", DELIVERIES_FOR_USER_ID);
        sURIMatcher.addURI(AUTHORITY, DELIVERY_PATH + "/*", DELIVERY_BY_ID);
    }

    public static final Uri DELIVERIES_FOR_URI = Uri.parse("content://" + AUTHORITY
            + "/" + DELIVERIES_FOR_PATH);
    public static final Uri DELIVERY_URI = Uri.parse("content://" + AUTHORITY
            + "/" + DELIVERY_PATH);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/" + DELIVERIES_FOR_PATH;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/" + DELIVERY_PATH;

    private DatabaseHandler mDb;

    @Override
    public boolean onCreate() {
        mDb = new DatabaseHandler(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(SoundDeliveryData.SQL_TABLE_NAME + ", " +
                SoundData.SQL_TABLE_NAME);

        String input = uri.getLastPathSegment();
        switch (sURIMatcher.match(uri)) {
            case DELIVERIES_FOR_USER_ID:
                String soundIdColumn = SoundData.SQL_TABLE_NAME +
                        "." + SoundData.SQL_ID;
                String soundDeliverySoundIdColumn = SoundDeliveryData.SQL_TABLE_NAME +
                        "." + SoundDeliveryData.SQL_SOUND_ID;
                String userIdColumn = SoundDeliveryData.SQL_TABLE_NAME + "." +
                        SoundDeliveryData.SQL_USER_ID;
                String recipientUserIdColumn = SoundDeliveryData.SQL_TABLE_NAME + "." +
                        SoundDeliveryData.SQL_RECIPIENT_USER_ID;
                queryBuilder.appendWhere(
                        soundIdColumn + "=" + soundDeliverySoundIdColumn + " AND " +
                        "(" + userIdColumn + "=\"" + input + "\" OR " +
                                recipientUserIdColumn + "=\"" + input + "\")");
                break;
            case DELIVERY_BY_ID:
                queryBuilder.appendWhere(SoundDeliveryData.SQL_ID + "=" + input);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }

        Cursor cursor = queryBuilder.query(mDb.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case DELIVERIES_FOR_USER_ID:
                return CONTENT_TYPE;
            case DELIVERY_BY_ID:
                return CONTENT_ITEM_TYPE;
        }
        throw new IllegalArgumentException("Unknown URI");
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        throw new IllegalArgumentException("Unsupported operation");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDb.getWritableDatabase();
        int rowsAffected = 0;
        switch (sURIMatcher.match(uri)) {
            case DELIVERIES_FOR_USER_ID:
                throw new IllegalArgumentException("Unsupported operation");
            case DELIVERY_BY_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsAffected = db.delete(SoundDeliveryData.SQL_TABLE_NAME,
                            SoundDeliveryData.SQL_ID + "=" + id, null);
                } else {
                    rowsAffected = db.delete(SoundDeliveryData.SQL_TABLE_NAME,
                            selection + " and " + SoundDeliveryData.SQL_ID + "=" + id,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        throw new IllegalArgumentException("Unsupported operation");
    }
}
