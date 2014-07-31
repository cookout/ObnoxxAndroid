package com.obnoxx.androidapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * The initial activity for Obnoxx.  Basically just acts as air-traffic control
 * and sends the user to login or the main application depending on whether
 * he has logged in before.  (The logic for this is in InitFragment.)
 */
public class InitActivity extends SingleFragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CurrentUser.maybeFetchRegistrationId(this);
    }

    @Override
    protected Fragment createFragment() {
        return new InitFragment();
    }
}
