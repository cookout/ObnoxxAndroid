package com.obnoxx.androidapp.ui;

import android.support.v4.app.Fragment;

import com.obnoxx.androidapp.data.ContactGroup;

/**
 * Activity for picking folks to share with.
 */
public class ContactPickerActivity extends SingleFragmentActivity {
    public static final String INITIAL_CONTACT_GROUP = "iContactGroup";

    @Override
    protected Fragment createFragment() {
        ContactPickerFragment fragment = new ContactPickerFragment();
        ContactGroup initialContactGroup =
                this.getIntent().getParcelableExtra(INITIAL_CONTACT_GROUP);
        if (initialContactGroup != null) {
            fragment.setInitialContactGroup(initialContactGroup);
        }
        return fragment;
    }
}
