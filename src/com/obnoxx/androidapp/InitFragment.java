package com.obnoxx.androidapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.IOException;

public class InitFragment extends Fragment {
    private static final String TAG = "InitFragment";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_init, parent, false);
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
