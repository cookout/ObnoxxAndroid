package com.obnoxx.androidapp.ui;

import android.content.Intent;
import android.os.Bundle;
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
