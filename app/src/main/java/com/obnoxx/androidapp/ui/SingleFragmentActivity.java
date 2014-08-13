package com.obnoxx.androidapp.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.view.WindowManager;

import com.obnoxx.androidapp.R;

/**
 * Abstract top-level Activity class that helps with the construction of
 * Fragment-based activities.  Basically just constructs the underlying
 * Fragment, as specified in the subclass by createFragment, and then
 * uses it as the entire view's controller.
 */
public abstract class SingleFragmentActivity extends ActionBarActivity {
    protected abstract Fragment createFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Hide the window title.
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        // Hide the status bar and other OS-level chrome
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.single_fragment_activity);
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = createFragment();
            manager.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }
}
