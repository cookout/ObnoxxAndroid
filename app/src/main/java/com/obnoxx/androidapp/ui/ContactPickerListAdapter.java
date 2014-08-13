package com.obnoxx.androidapp.ui;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.obnoxx.androidapp.R;

public class ContactPickerListAdapter extends CursorAdapter {
    private static final String TAG = "ContactPickerListAdapter";
    private final Activity mActivity;
    private final ContactPickerModel mModel;

    public ContactPickerListAdapter(Activity activity, ContactPickerModel model) {
        super(activity, /* cursor */ null, 0);
        mActivity = activity;
        mModel = model;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.contact_picker_listitem, parent, false);
        bindView(v, context, cursor);
        return v;
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        // Top-level.
        boolean isSelected = mModel.isSelected(cursor.getString(cursor.getColumnIndex(
                ContactsContract.Contacts._ID)));
        view.setTag(new Integer(cursor.getPosition()));
        view.setSelected(isSelected);

        // Contact name.
        ((TextView) view.findViewById(R.id.name))
                .setText(cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME)));

        // Phone number.
        ((TextView) view.findViewById(R.id.phoneNumber))
                .setText(cursor.getString(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER)));

        // Check box.
        ((CheckBox) view.findViewById(R.id.checkbox)).setChecked(isSelected);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursor.moveToPosition((Integer) view.getTag());
                String id = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts._ID));

                CheckBox checkBox = ((CheckBox) view.findViewById(R.id.checkbox));
                boolean wasChecked = checkBox.isChecked();
                checkBox.setChecked(!wasChecked);
                view.setSelected(!wasChecked);
                if (wasChecked) {
                    mModel.deselect(id);
                } else {
                    mModel.setSelected(new Contact(id,
                            cursor.getString(cursor.getColumnIndex(
                                    ContactsContract.Contacts.DISPLAY_NAME)),
                            cursor.getString(cursor.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER))
                    ));
                }
            }
        });
    }
}