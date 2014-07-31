package com.obnoxx.androidapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;

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
