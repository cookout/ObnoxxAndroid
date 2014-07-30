package com.obnoxx.androidapp;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class RecordSoundFragment extends Fragment {
    private static final String TAG = "RecordSoundFragment";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private static int OUTPUT_FORMATS[] = {
            MediaRecorder.OutputFormat.MPEG_4,
            MediaRecorder.OutputFormat.THREE_GPP
    };
    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
    private static String FILE_EXTENSIONS[] = {
            AUDIO_RECORDER_FILE_EXT_MP4,
            AUDIO_RECORDER_FILE_EXT_3GP
    };

    private MediaRecorder mRecorder = null;
    private int mCurrentFormat = 0; // TODO(jonemerson): Figure out what we're trying to do here.

    @Override
    @SuppressWarnings("deprecation")
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
                    Toast.makeText(RecordSoundFragment.this.getActivity(), "Start Recording",
                            Toast.LENGTH_SHORT).show();
                    startRecording();

                    // Stop recording in 3s
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            enableButton(R.id.btnPlay, true);
                            stopRecording();
                        }
                    }, 3000);
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

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(OUTPUT_FORMATS[mCurrentFormat]);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(getFilename());
        mRecorder.setOnErrorListener(mErrorListener);
        mRecorder.setOnInfoListener(mInfoListener);
        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        Toast.makeText(RecordSoundFragment.this.getActivity(),
                "Stop Recording", Toast.LENGTH_SHORT).show();
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        }
    }

    private void play() {
        Toast.makeText(RecordSoundFragment.this.getActivity(),
                "Playing...", Toast.LENGTH_SHORT).show();
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(getFilename());
            mediaPlayer.prepare();
        } catch (IOException e) {
            Toast.makeText(RecordSoundFragment.this.getActivity(),
                    "Error Playing...", Toast.LENGTH_SHORT).show();
        }
        mediaPlayer.start();
    }

    private void enableButton(int id, boolean isEnable) {
        ((Button) getActivity().findViewById(id)).setEnabled(isEnable);
    }

    private String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
        return (file.getAbsolutePath() + "/current" + FILE_EXTENSIONS[mCurrentFormat]);
    }

    private void send() {
        SendHttpRequestTask t = new SendHttpRequestTask(
                this.getActivity(), getFilename(), mCurrentFormat);
        t.execute();
    }

    private MediaRecorder.OnErrorListener mErrorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Toast.makeText(RecordSoundFragment.this.getActivity(),
                    "Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
        }
    };

    private MediaRecorder.OnInfoListener mInfoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            Toast.makeText(RecordSoundFragment.this.getActivity(),
                    "Warning: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
        }
    };
}
