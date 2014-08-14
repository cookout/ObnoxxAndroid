package com.obnoxx.androidapp.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Iterator;

public class ContactGroup implements Parcelable {
    private final HashMap<String, ContactData> mContacts = new HashMap<String, ContactData>();

    public ContactGroup() {
    }

    public boolean contains(String id) {
        return mContacts.containsKey(id);
    }

    public void add(ContactData contact) {
        mContacts.put(contact.getId(), contact);
    }

    public void remove(String id) {
        mContacts.remove(id);
    }

    public ContactGroup clone() {
        ContactGroup clone = new ContactGroup();
        clone.copyFrom(this);
        return clone;
    }

    public void copyFrom(ContactGroup group) {
        mContacts.clear();
        mContacts.putAll(group.mContacts);
    }

    public String getPhoneNumbersString() {
        StringBuilder b = new StringBuilder();
        for (ContactData contactData : mContacts.values()) {
            if (b.length() > 0) {
                b.append(",");
            }
            b.append(contactData.getPhoneNumber());
        }
        return b.toString();
    }

    @Override
    public String toString() {
        String[] names = new String[3];
        Iterator<ContactData> dataIterator = mContacts.values().iterator();
        for (int i = 0; i < names.length; i++) {
            if (dataIterator.hasNext()) {
                names[i] = dataIterator.next().getName();
            } else {
                names[i] = null;
            }
        }

        switch (mContacts.size()) {
            case 0:
                return "No one";
            case 1:
                return names[0];
            case 2:
                return names[0] + " and " + names[1];
            default:
                return names[0] + ", " + names[1] + " and " + (mContacts.size() - 2) +
                        " more people";
        }
    }

    public static final Parcelable.Creator<ContactGroup> CREATOR =
            new Parcelable.Creator<ContactGroup>() {
                public ContactGroup createFromParcel(Parcel in) {
                    ContactGroup group = new ContactGroup();
                    int numContacts = in.readInt();
                    for (int i = 0; i < numContacts; i++) {
                        group.add(ContactData.CREATOR.createFromParcel(in));
                    }
                    return group;
                }

                public ContactGroup[] newArray(int size) {
                    return new ContactGroup[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mContacts.size());
        for (ContactData contact : mContacts.values()) {
            contact.writeToParcel(parcel, flags);
        }
    }

    public void save(Context context) {
        SQLiteDatabase db = new DatabaseHandler(context).getWritableDatabase();
        db.delete(ContactData.SQL_TABLE_NAME, /* whereClause */ null, /* whereArgs */ null);
        for (ContactData contactData : mContacts.values()) {
            db.insert(ContactData.SQL_TABLE_NAME, null, contactData.toValues());
        }
    }

    public static ContactGroup get(Context context) {
        SQLiteDatabase db = new DatabaseHandler(context).getReadableDatabase();
        String[] columns = {
                ContactData.SQL_ID,
                ContactData.SQL_NAME,
                ContactData.SQL_PHONE_NUMBER,
        };
        Cursor cursor = db.query(ContactData.SQL_TABLE_NAME,
                columns,
                /* selection */ null,
                /* selectionArgs */ null,
                /* groupBy */ null,
                /* having */ null,
                /* orderBy */ null);

        ContactGroup group = new ContactGroup();
        while (cursor.moveToNext()) {
            group.add(new ContactData.Builder()
                    .setId(cursor.getString(cursor.getColumnIndex(ContactData.SQL_ID)))
                    .setName(cursor.getString(cursor.getColumnIndex(ContactData.SQL_NAME)))
                    .setPhoneNumber(cursor.getString(cursor.getColumnIndex(
                            ContactData.SQL_PHONE_NUMBER)))
                    .build());
        }
        return group;
    }
}
