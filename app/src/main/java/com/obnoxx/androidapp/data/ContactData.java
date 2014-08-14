package com.obnoxx.androidapp.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

public class ContactData implements Parcelable {
    // Local SQLite Database field names.
    public static final String SQL_TABLE_NAME = "Contact";
    public static final String SQL_ID = "id";
    public static final String SQL_NAME = "name";
    public static final String SQL_PHONE_NUMBER = "phoneNumber";
    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + SQL_TABLE_NAME + " (" +
                    SQL_ID + " TEXT PRIMARY KEY, " +
                    SQL_NAME + " TEXT, " +
                    SQL_PHONE_NUMBER + " TEXT);";

    private String mId;
    private String mName;
    private String mPhoneNumber;

    public static class Builder {
        private String mId;
        private String mName;
        private String mPhoneNumber;

        public Builder() {
        }

        public Builder(ContactData contactData) {
            this.mId = contactData.mId;
            this.mName = contactData.mName;
            this.mPhoneNumber = contactData.mPhoneNumber;
        }

        public Builder setId(String id) {
            this.mId = id;
            return this;
        }

        public Builder setName(String name) {
            this.mName = name;
            return this;
        }

        public Builder setPhoneNumber(String phoneNumber) {
            this.mPhoneNumber = phoneNumber;
            return this;
        }

        public ContactData build() {
            ContactData contactData = new ContactData();
            contactData.mId = mId;
            contactData.mName = mName;
            contactData.mPhoneNumber = mPhoneNumber;
            return contactData;
        }
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mId);
        parcel.writeString(mName);
        parcel.writeString(mPhoneNumber);
    }

    public ContentValues toValues() {
        ContentValues v = new ContentValues();
        v.put(SQL_ID, mId);
        v.put(SQL_NAME, mName);
        v.put(SQL_PHONE_NUMBER, mPhoneNumber);
        return v;
    }

    public static final Parcelable.Creator<ContactData> CREATOR =
            new Parcelable.Creator<ContactData>() {
                public ContactData createFromParcel(Parcel in) {
                    return new Builder()
                            .setId(in.readString())
                            .setName(in.readString())
                            .setPhoneNumber(in.readString())
                            .build();
                }

                public ContactData[] newArray(int size) {
                    return new ContactData[size];
                }
            };

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(SQL_CREATE_TABLE);
    }
}
