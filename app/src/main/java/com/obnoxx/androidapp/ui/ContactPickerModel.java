package com.obnoxx.androidapp.ui;

import android.os.Bundle;
import android.os.Parcelable;

import java.util.Collection;
import java.util.HashMap;

public class ContactPickerModel {
    private static final String CONTACTS_BUNDLE_KEY = "contacts";
    private final HashMap<String, Contact> mSelectedContacts = new HashMap<String, Contact>();

    public boolean isSelected(String id) {
        return mSelectedContacts.containsKey(id);
    }

    public void setSelected(Contact contact) {
        mSelectedContacts.put(contact.getId(), contact);
    }

    public void deselect(String id) {
        mSelectedContacts.remove(id);
    }

    public ContactPickerModel(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            for (Parcelable parcelable :
                    savedInstanceState.getParcelableArray(CONTACTS_BUNDLE_KEY)) {
                Contact contact = (Contact) parcelable;
                mSelectedContacts.put(contact.getId(), contact);
            }
        }
    }

    public Bundle getSavedInstanceState() {
        Bundle b = new Bundle();
        Collection contacts = mSelectedContacts.values();
        b.putParcelableArray(CONTACTS_BUNDLE_KEY,
                (Contact[]) contacts.toArray(new Contact[contacts.size()]));
        return b;
    }
}
