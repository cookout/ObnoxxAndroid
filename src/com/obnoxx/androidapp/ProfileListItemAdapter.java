package com.obnoxx.androidapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.obnoxx.androidapp.data.DatabaseHandler;
import com.obnoxx.androidapp.data.Sound;
import com.obnoxx.androidapp.data.SoundData;
import com.obnoxx.androidapp.data.SoundDelivery;
import com.obnoxx.androidapp.data.SoundDeliveryData;

import java.util.Date;

public class ProfileListItemAdapter extends CursorAdapter {
    private static final String TAG = "ProfileListItemAdapter";
    private static final String SOUND_ID_COLUMN_NAME = "sound_id";
    private static final String SOUND_DELIVERY_ID_COLUMN_NAME = "sound_delivery_id";
    public static final String SOUND_DELIVERY_DATE_TIME_COLUMN_NAME = "sound_delivery_date_time";
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
            SoundDeliveryData.SQL_TABLE_NAME + "._rowid_ as " +
                    DatabaseHandler.CURSOR_ID,
            SoundData.SQL_TABLE_NAME + "." + SoundData.SQL_ID + " as " +
                    SOUND_ID_COLUMN_NAME,
            SoundDeliveryData.SQL_TABLE_NAME + "." +
                    SoundDeliveryData.SQL_ID + " as " + SOUND_DELIVERY_ID_COLUMN_NAME,
            SoundDeliveryData.SQL_TABLE_NAME + "." +
                    SoundDeliveryData.SQL_DATE_TIME + " as " +
                    SOUND_DELIVERY_DATE_TIME_COLUMN_NAME,
            SoundDeliveryData.SQL_TABLE_NAME + "." +
                    SoundDeliveryData.SQL_USER_ID + " as " +
                    RECIPIENT_USER_ID_COLUMN_NAME,
            SoundDeliveryData.SQL_TABLE_NAME + "." +
                    SoundDeliveryData.SQL_PHONE_NUMBER + " as " +
                    PHONE_NUMBER_COLUMN_NAME,
            SoundDeliveryData.SQL_TABLE_NAME + "." +
                    SoundDeliveryData.SQL_USER_ID + " as " +
                    USER_ID_COLUMN_NAME,
            SoundData.SQL_TABLE_NAME + "." +
                    SoundData.SQL_FILE_URL + " as " +
                    SOUND_FILE_URL_COLUMN_NAME,
            SoundData.SQL_TABLE_NAME + "." +
                    SoundData.SQL_LOCAL_FILE_PATH + " as " +
                    LOCAL_FILE_PATH_COLUMN_NAME,
            SoundData.SQL_TABLE_NAME + "." +
                    SoundData.SQL_CREATE_DATE_TIME + " as " +
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

        return new SoundDelivery(new SoundDeliveryData.Builder()
                .setId(cursor.getString(cursor.getColumnIndex(SOUND_DELIVERY_ID_COLUMN_NAME)))
                .setSoundId(cursor.getString(cursor.getColumnIndex(SOUND_ID_COLUMN_NAME)))
                .setRecipientUserId(cursor.getString(cursor.getColumnIndex(RECIPIENT_USER_ID_COLUMN_NAME)))
                .setPhoneNumber(cursor.getString(cursor.getColumnIndex(PHONE_NUMBER_COLUMN_NAME)))
                .setDeliveryDate(DateHelper.parse(cursor.getString(
                        cursor.getColumnIndex(SOUND_DELIVERY_DATE_TIME_COLUMN_NAME))))
                .setUserId(cursor.getString(cursor.getColumnIndex(USER_ID_COLUMN_NAME)))
                .build());
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
        Date d = DateHelper.parse(s);

        return new Sound(new SoundData.Builder()
                .setId(cursor.getString(cursor.getColumnIndex(SOUND_ID_COLUMN_NAME)))
                .setUserId(cursor.getString(cursor.getColumnIndex(USER_ID_COLUMN_NAME)))
                .setSoundFileUrl(cursor.getString(cursor.getColumnIndex(SOUND_FILE_URL_COLUMN_NAME)))
                .setLocalFilePath(cursor.getString(cursor.getColumnIndex(LOCAL_FILE_PATH_COLUMN_NAME)))
                .setCreateDate(DateHelper.parse(cursor.getString(
                        cursor.getColumnIndex(SOUND_CREATE_DATE_COLUMN_NAME))))
                .build());
    }
}