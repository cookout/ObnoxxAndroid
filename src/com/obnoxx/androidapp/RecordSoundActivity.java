package com.obnoxx.androidapp;

import android.support.v4.app.Fragment;

/**
 * Top-level activity for recording a sound to share with recipients.
 */
public class RecordSoundActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
       return new RecordSoundFragment();
    }
}
