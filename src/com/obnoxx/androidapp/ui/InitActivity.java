package com.obnoxx.androidapp.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.obnoxx.androidapp.CurrentUser;
import com.obnoxx.androidapp.GetSoundsOperation;

/**
 * The initial activity for Obnoxx.  Basically just acts as air-traffic control
 * and sends the user to login or the main application depending on whether
 * he has logged in before.  (The logic for this is in InitFragment.)
 */
public class InitActivity extends SingleFragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register for push notifications.
        CurrentUser.maybeFetchRegistrationId(this);

        // Preload our database with sounds / deliveries / users relevant to
        // the current user.d
        if (CurrentUser.hasSessionId(this)) {
            new GetSoundsOperation(this);
        }
    }

    @Override
    protected Fragment createFragment() {
        return new InitFragment();
    }
}
