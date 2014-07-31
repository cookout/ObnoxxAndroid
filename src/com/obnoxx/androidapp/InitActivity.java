package com.obnoxx.androidapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

public class InitActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new InitFragment();
    }
}
