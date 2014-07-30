package com.obnoxx.androidapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

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
        View v = inflater.inflate(R.layout.fragment_record_sound, parent, false);

        setButtonHandlers(v);

        return v;
    }

    private void setButtonHandlers(View v) {
        ((Button) v.findViewById(R.id.btnRecord)).setOnClickListener(onClickListener);
        ((Button) v.findViewById(R.id.btnPlay)).setOnClickListener(onClickListener);
        ((Button) v.findViewById(R.id.btnSend)).setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnRecord:
                    try {
                        mSoundRecorder.start();
                        Toast.makeText(RecordSoundFragment.this.getActivity(), "Start Recording",
                                Toast.LENGTH_SHORT).show();
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
            }
        }
    };

    private void play() {
        Toast.makeText(RecordSoundFragment.this.getActivity(),
                "Playing...", Toast.LENGTH_SHORT).show();
    }

    private void enableButton(int id, boolean isEnable) {
        ((Button) getActivity().findViewById(id)).setEnabled(isEnable);
    }

    private void send() {
        Sound sound = mSoundRecorder.getLastSound();
        if (sound != null) {
            SendHttpRequestTask t = new SendHttpRequestTask(
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
