package com.obnoxx.androidapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.obnoxx.androidapp.R;
import com.obnoxx.androidapp.SoundRecorder;
import com.obnoxx.androidapp.SoundRecordingException;
import com.obnoxx.androidapp.data.Sound;
import com.obnoxx.androidapp.requests.AddSoundRequest;

/**
 * Implements the sound recording interface.  Lets the user record a sound,
 * play it back, and choose people to share it with.
 */
public class RecordSoundFragment extends Fragment {
    private static final String TAG = "RecordSoundFragment";

    private SoundRecorder mSoundRecorder;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mSoundRecorder = new SoundRecorder(activity.getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.record_sound_fragment, parent, false);

        setButtonHandlers(v);

        return v;
    }

    private void setButtonHandlers(View v) {
        ((Button) v.findViewById(R.id.btnRecord)).setOnClickListener(onClickListener);
        ((Button) v.findViewById(R.id.btnPlay)).setOnClickListener(onClickListener);
        ((Button) v.findViewById(R.id.btnSend)).setOnClickListener(onClickListener);
        ((Button) v.findViewById(R.id.button_profile)).setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnRecord:
                    try {
                        mSoundRecorder.start();
                    } catch (SoundRecordingException e) {
                        Log.e(TAG, "Could not start recording", e);
                    }
                    break;

                case R.id.btnPlay:
                    play();
                    break;

                case R.id.btnSend:
                    send();
                    break;

                case R.id.button_profile:
                    startActivity(new Intent(RecordSoundFragment.this.getActivity(),
                            ProfileActivity.class));
                    break;
            }
        }
    };

    private void play() {
        Toast.makeText(this.getActivity(), "Playing...", Toast.LENGTH_SHORT).show();
        Sound sound = mSoundRecorder.getLastSound();
        if (sound != null) {
            sound.play();
        }
    }

    private void send() {
        Sound sound = mSoundRecorder.getLastSound();
        if (sound != null) {
            AddSoundRequest t = new AddSoundRequest(
                    this.getActivity(), sound, getSelectedPhoneNumber());
            t.execute();
        }
    }

    private String getSelectedPhoneNumber() {
        if (((RadioButton) getActivity().findViewById((R.id.radio_jon))).isChecked()) {
            return "+14157068528";
        } else if (((RadioButton) getActivity().findViewById((R.id.radio_chandra))).isChecked()) {
            return "+16507205269";
        } else if (((RadioButton) getActivity().findViewById((R.id.radio_oliver))).isChecked()) {
            return "+14153163345";
        }
        throw new IllegalStateException("Should not happen");
    }
}
