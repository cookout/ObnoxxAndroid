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

    private final Context mAppContext;
    private MediaRecorder mMediaRecorder = null;
    private int mCurrentFormat = 0; // TODO(jonemerson): Figure out what we're trying to do here.
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
        try {
            // Initialize.
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            // DataSourceConfigure.
            mMediaRecorder.setOutputFormat(OUTPUT_FORMATS[mCurrentFormat]);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.setOnErrorListener(mErrorListener);
            mMediaRecorder.setOnInfoListener(mInfoListener);
            mMediaRecorder.setOutputFile(mCurrentFilename);
            mMediaRecorder.setMaxDuration(3 * 1000);

            // Prepare.
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IllegalStateException e) {
            throw new SoundRecordingException(e);
        } catch (IOException e) {
            throw new SoundRecordingException(e);
        }
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

    private String getNewFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(),
                AUDIO_RECORDER_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
        return (file.getAbsolutePath() + "/audio" + System.currentTimeMillis() +
                FILE_EXTENSIONS[mCurrentFormat]);
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
                SoundRecorder.this.sounds.add(new Sound(mCurrentFilename));
                mCurrentFilename = null;
                Toast.makeText(mAppContext, "Recorded!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mAppContext, "Info: " + what + ", " + extra, Toast.LENGTH_SHORT)
                        .show();
            }
        }
    };
}
