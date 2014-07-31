package com.obnoxx.androidapp;

import android.support.v4.app.Fragment;

public class RecordSoundActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
       return new RecordSoundFragment();
    }
}

