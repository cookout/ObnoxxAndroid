package com.obnoxx.androidapp;

import android.support.v4.app.Fragment;

/**
 * This activity renders a user's profile.  Right now it's only implemented for the current user,
 * and it only shows a history of his sound deliveries - outgoing and incoming.  But it will grow
 * to have a larger role.
 */
public class ProfileActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new ProfileFragment();
    }
}
