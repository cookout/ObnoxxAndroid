package com.obnoxx.androidapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Date;

public class ProfileListItemAdapter extends CursorAdapter {
    private static final String TAG = "ProfileListItemAdapter";
    private static final String SOUND_ID_COLUMN_NAME = "sound_id";
    private static final String SOUND_DELIVERY_ID_COLUMN_NAME = "sound_delivery_id";
    private static final String SOUND_DELIVERY_DATE_TIME_COLUMN_NAME = "sound_delivery_date_time";
    private static final String RECIPIENT_USER_ID_COLUMN_NAME = "sound_delivery_recipient_user_id";
    private static final String PHONE_NUMBER_COLUMN_NAME = "sound_delivery_phone_number";
    private static final String USER_ID_COLUMN_NAME = "sound_delivery_user_id";
    private static final String SOUND_FILE_URL_COLUMN_NAME = "sound_file_url";
    private static final String LOCAL_FILE_PATH_COLUMN_NAME = "local_file_path";
    private static final String SOUND_CREATE_DATE_COLUMN_NAME = "sound_create_date";

    /**
     * These are the columns from SoundDeliveryProvider that this list adapter
     * needs to render sound deliveries in profile_list_item rows.
     */
    public static final String[] PROJECTION = {
            DatabaseHandler.SOUND_DELIVERY_TABLE_NAME + "._rowid_ as " +
                    DatabaseHandler.CURSOR_ID,
            DatabaseHandler.SOUND_TABLE_NAME + "." + DatabaseHandler.SOUND_ID + " as " +
                    SOUND_ID_COLUMN_NAME,
            DatabaseHandler.SOUND_DELIVERY_TABLE_NAME + "." +
                    DatabaseHandler.SOUND_DELIVERY_ID + " as " + SOUND_DELIVERY_ID_COLUMN_NAME,
            DatabaseHandler.SOUND_DELIVERY_TABLE_NAME + "." +
                    DatabaseHandler.SOUND_DELIVERY_DATE_TIME + " as " +
                    SOUND_DELIVERY_DATE_TIME_COLUMN_NAME,
            DatabaseHandler.SOUND_DELIVERY_TABLE_NAME + "." +
                    DatabaseHandler.SOUND_DELIVERY_USER_ID + " as " +
                    RECIPIENT_USER_ID_COLUMN_NAME,
            DatabaseHandler.SOUND_DELIVERY_TABLE_NAME + "." +
                    DatabaseHandler.SOUND_DELIVERY_PHONE_NUMBER + " as " +
                    PHONE_NUMBER_COLUMN_NAME,
            DatabaseHandler.SOUND_DELIVERY_TABLE_NAME + "." +
                    DatabaseHandler.SOUND_DELIVERY_USER_ID + " as " +
                    USER_ID_COLUMN_NAME,
            DatabaseHandler.SOUND_TABLE_NAME + "." +
                    DatabaseHandler.SOUND_FILE_URL + " as " +
                    SOUND_FILE_URL_COLUMN_NAME,
            DatabaseHandler.SOUND_TABLE_NAME + "." +
                    DatabaseHandler.SOUND_LOCAL_FILE_PATH + " as " +
                    LOCAL_FILE_PATH_COLUMN_NAME,
            DatabaseHandler.SOUND_TABLE_NAME + "." +
                    DatabaseHandler.SOUND_CREATE_DATE_TIME + " as " +
                    SOUND_CREATE_DATE_COLUMN_NAME
    };

    public ProfileListItemAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.profile_list_item, parent, false);
        bindView(v, context, cursor);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView = ((TextView) view.findViewById(R.id.title));
        textView.setText(
                cursor.getString(cursor.getColumnIndex(SOUND_DELIVERY_DATE_TIME_COLUMN_NAME)));
    }

    public SoundDelivery getSoundDeliveryForPosition(int position) {
        Cursor cursor = this.getCursor();
        cursor.moveToPosition(position);

        return new SoundDelivery.Builder()
                .setId(cursor.getString(cursor.getColumnIndex(SOUND_DELIVERY_ID_COLUMN_NAME)))
                .setSoundId(cursor.getString(cursor.getColumnIndex(SOUND_ID_COLUMN_NAME)))
                .setRecipientUserId(cursor.getString(cursor.getColumnIndex(RECIPIENT_USER_ID_COLUMN_NAME)))
                .setPhoneNumber(cursor.getString(cursor.getColumnIndex(PHONE_NUMBER_COLUMN_NAME)))
                .setDeliveryDate(createDate(cursor.getString(
                        cursor.getColumnIndex(SOUND_DELIVERY_DATE_TIME_COLUMN_NAME))))
                .setUserId(cursor.getString(cursor.getColumnIndex(USER_ID_COLUMN_NAME)))
                .build();
    }

    public Sound getSoundForPosition(int position) {
        Cursor cursor = this.getCursor();
        cursor.moveToPosition(position);

        int i = cursor.getColumnIndex(SOUND_ID_COLUMN_NAME);
        String s = cursor.getString(i);
        i = cursor.getColumnIndex(USER_ID_COLUMN_NAME);
        s = cursor.getString(i);
        i = cursor.getColumnIndex(SOUND_FILE_URL_COLUMN_NAME);
        s = cursor.getString(i);
        i = cursor.getColumnIndex(LOCAL_FILE_PATH_COLUMN_NAME);
        s = cursor.getString(i);
        i = cursor.getColumnIndex(SOUND_CREATE_DATE_COLUMN_NAME);
        s = cursor.getString(i);
        Date d = createDate(s);

        return new Sound.Builder()
                .setId(cursor.getString(cursor.getColumnIndex(SOUND_ID_COLUMN_NAME)))
                .setUserId(cursor.getString(cursor.getColumnIndex(USER_ID_COLUMN_NAME)))
                .setSoundFileUrl(cursor.getString(cursor.getColumnIndex(SOUND_FILE_URL_COLUMN_NAME)))
                .setLocalFilePath(cursor.getString(cursor.getColumnIndex(LOCAL_FILE_PATH_COLUMN_NAME)))
                .setCreateDate(createDate(cursor.getString(
                        cursor.getColumnIndex(SOUND_CREATE_DATE_COLUMN_NAME))))
                .build();
    }

    /**
     * Parses a date.
     * TODO(jonemerson): Find a place to put some global Date handling utilities.
     */
    private Date createDate(String dateStr) {
        Date deliveryDate = null;
        try {
            return SoundDelivery.DATE_TIME_FORMATTER.parse(dateStr);
        } catch (ParseException e) {
            Log.e(TAG, "Could not parse date: " + dateStr, e);
            return new Date();
        }
    }
}