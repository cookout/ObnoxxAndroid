package com.obnoxx.androidapp;

public class SoundRecordingException extends Exception {
    public SoundRecordingException(String message) {
        super(message);
    }

    public SoundRecordingException(Exception e) {
        super(e);
    }

    public SoundRecordingException(String message, Exception e) {
        super(message, e);
    }
}
