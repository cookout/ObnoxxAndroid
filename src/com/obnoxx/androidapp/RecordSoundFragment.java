package com.obnoxx.androidapp;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.IOException;

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
        Toast.makeText(this.getActivity(), "Playing...", Toast.LENGTH_SHORT).show();
        mSoundRecorder.getLastSound().play();
    }

    // TODO(jonemerson): Delete this, but this is the shortest code I have for recording a sound
    // and then playing it back one second later.  So checking it in for future debugging purposes.
    private void play2() {
        try {
            final String filename = Environment.getExternalStorageDirectory().getPath() +
                    "/tempaudio.3gp";
            final MediaRecorder recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(filename);
            recorder.setMaxDuration(3 * 1000);
            recorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mediaRecorder, int what, int i2) {
                    if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                        Toast.makeText(RecordSoundFragment.this.getActivity(), "Recording ended!",
                                Toast.LENGTH_SHORT).show();
                        recorder.release();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RecordSoundFragment.this.getActivity(),
                                        "Playback started!", Toast.LENGTH_SHORT).show();
                                MediaPlayer player = new MediaPlayer();
                                try {
                                    player.setDataSource(filename);
                                    player.prepare();
                                    player.start();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                player.start();
                            }
                        }, 1000);
                    }
                }
            });
            recorder.prepare();
            recorder.start();
            Toast.makeText(this.getActivity(), "Recording started!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
