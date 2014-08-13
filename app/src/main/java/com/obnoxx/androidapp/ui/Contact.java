package com.obnoxx.androidapp.ui;

import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable {
    private String mId;
    private String mName;
    private String mPhoneNumber;

    public Contact(String id, String name, String phoneNumber) {
        mId = id;
        mName = name;
        mPhoneNumber = phoneNumber;
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

    public static final Parcelable.Creator<Contact> CREATOR =
            new Parcelable.Creator<Contact>() {
                public Contact createFromParcel(Parcel in) {
                    return new Contact(in.readString(), in.readString(), in.readString());
                }

                public Contact[] newArray(int size) {
                    return new Contact[size];
                }
            };
}
