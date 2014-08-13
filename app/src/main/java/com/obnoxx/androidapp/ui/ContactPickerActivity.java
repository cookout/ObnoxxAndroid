package com.obnoxx.androidapp.ui;

import android.support.v4.app.Fragment;

/**
 * Activity for picking folks to share with.
 */
public class ContactPickerActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new ContactPickerFragment();
    }
}
