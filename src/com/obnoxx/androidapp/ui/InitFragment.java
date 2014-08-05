package com.obnoxx.androidapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.obnoxx.androidapp.CurrentUser;
import com.obnoxx.androidapp.R;

/**
 * The initial fragment for Obnoxx.  Basically just acts as air-traffic control
 * and sends the user to login or the main application depending on whether
 * he has logged in before.
 */
public class InitFragment extends Fragment {
    private static final String TAG = "InitFragment";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.init_fragment, parent, false);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Show the best view to start with: If the user's logged in, let them record a sound.
        // If the user's not logged in, have them log in first.
        if (CurrentUser.hasSessionId(getActivity())) {
            startActivity(new Intent(getActivity(), RecordSoundActivity.class));
        } else {
            startActivity(new Intent(getActivity(), VerifyPhoneNumberActivity.class));
        }
    }
}
