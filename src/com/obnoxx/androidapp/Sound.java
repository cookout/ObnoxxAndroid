package com.obnoxx.androidapp;

import android.media.MediaPlayer;
import android.widget.Toast;

import java.io.IOException;

/**
 * A previously recorded sound that can be played back.
 */
public class Sound {
    private final String mFilename;
    private static MediaPlayer mPlayer = null;

    public Sound(String filename) {
        mFilename = filename;
    }

    public String getFilename() {
        return mFilename;
    }

    public void play() {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        }

        try {
            mPlayer.setDataSource(mFilename);
            mPlayer.prepare();
        } catch (IOException e) {
            throw new RuntimeException("Error playing back sound", e);
        }
        mPlayer.start();
    }
}
