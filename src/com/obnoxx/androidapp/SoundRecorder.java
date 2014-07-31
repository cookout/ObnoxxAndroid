package com.obnoxx.androidapp;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Wraps an Android MediaRecorder to provide a nice API for recording 3-second audio clips and
 * writing them to disk.  {@code start} starts recording, {@code stop} ends recording and returns
 * a Sound object that can be replayed and/or sent to the server.
 */
public class SoundRecorder {
    private static final String TAG = "SoundRecorder";

    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";

    private final Context mAppContext;
    private MediaRecorder mMediaRecorder = null;
    private boolean mIsStarted = false;
    private String mCurrentFilename = null;
    private List<Sound> sounds = new ArrayList<Sound>();

    public SoundRecorder(Context appContext) {
        mAppContext = appContext;
    }

    /**
     * Starts recording.
     * @throws SoundRecordingException
     */
    public void start() throws SoundRecordingException {
        if (mIsStarted) {
            throw new SoundRecordingException("Already started");
        }
        mIsStarted = true;

        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        }

        mCurrentFilename = getNewFilename();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mMediaRecorder.setOutputFile(mCurrentFilename);
        mMediaRecorder.setMaxDuration(3 * 1000);
        mMediaRecorder.setOnInfoListener(mInfoListener);
        mMediaRecorder.setOnErrorListener(mErrorListener);
        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mMediaRecorder.start();   // Recording is now started
        Toast.makeText(mAppContext, "Recording started!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Stops recording.
     * @throws SoundRecordingException
     */
    private void stop() throws SoundRecordingException {
        if (!mIsStarted) {
            throw new SoundRecordingException("Not started, can't be stopped.");
        }
        mIsStarted = false;

        mMediaRecorder.stop();
        // NOTE(jonemerson): No need to call reset() - Stop already clears everything out.
    }

    /**
     * Returns the most recent completed sound recording.
     */
    public Sound getLastSound() {
        return sounds.size() > 0 ? sounds.get(sounds.size() - 1) : null;
    }

    public static String getNewFilename() {
        // TODO(jonemerson): Is this the right directory for storing our private files?  Make sure
        // there isn't a more private place.
        File file = new File(Environment.getExternalStorageDirectory().getPath(),
                AUDIO_RECORDER_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
        return (file.getAbsolutePath() + "/audio" + System.currentTimeMillis() +
                AUDIO_RECORDER_FILE_EXT_3GP);
    }

    private MediaRecorder.OnErrorListener mErrorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Toast.makeText(mAppContext, "Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
        }
    };

    private MediaRecorder.OnInfoListener mInfoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                Toast.makeText(SoundRecorder.this.mAppContext, "Recording ended!",
                        Toast.LENGTH_SHORT).show();
                mMediaRecorder.reset();
                SoundRecorder.this.sounds.add(new Sound(mCurrentFilename));
                mCurrentFilename = null;
            } else {
                Toast.makeText(mAppContext, "Info: " + what + ", " + extra, Toast.LENGTH_SHORT)
                        .show();
            }
        }
    };
}
