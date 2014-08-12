package com.obnoxx.androidapp;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

import com.obnoxx.androidapp.data.Sound;
import com.obnoxx.androidapp.data.SoundData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final int[] SAMPLE_RATES = new int[] { 44100, 22050, 11025, 8000 };
    private static final int MAX_DURATION_MILLISECONDS = 3000;

    private final Context mAppContext;
    private AudioRecord mAudioRecord = null;
    private boolean mIsRecording = false;
    private String mCurrentFilename = null;
    private List<Sound> sounds = new ArrayList<Sound>();
    private Thread recordingThread = null;
    private int mBufferSize = 0;
    private int mSampleRate = 0;
    private short mChannels = 0;
    private short mAudioEncoding = 0;
    private String tempFilename = null;

    public SoundRecorder(Context appContext) {
        mAppContext = appContext;
    }

    /**
     * Starts recording.
     * @throws SoundRecordingException
     */
    public void start() throws SoundRecordingException {
        if (mIsRecording) {
            throw new SoundRecordingException("Already started");
        }
        mIsRecording = true;

        mAudioRecord = findAudioRecord();

        mAudioRecord.startRecording();

        recordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                writeAudioDataToFile();
            }
        },"AudioRecorder Thread");

        recordingThread.start();

        Toast.makeText(mAppContext, "Recording started!", Toast.LENGTH_SHORT).show();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    stop();
                } catch (Exception e) {

                }
            }
        }, MAX_DURATION_MILLISECONDS);
    }


    /**
     * Searches through audio settings until one works.
     * @return AudioRecord A new AudioRecord.
     */
    public AudioRecord findAudioRecord() {
        for (int sampleRate : SAMPLE_RATES) {
            for (short audioEncoding : new short[] { AudioFormat.ENCODING_PCM_16BIT, AudioFormat.ENCODING_PCM_8BIT }) {
                for (short channels : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO }) {
                    try {
                        mBufferSize = AudioRecord.getMinBufferSize(sampleRate, channels, audioEncoding);

                        if (mBufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            // check if we can instantiate and have a success
                            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channels, audioEncoding, mBufferSize);

                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
                            {
                                mSampleRate = sampleRate;
                                mAudioEncoding = audioEncoding;
                                mChannels = channels;
                                return recorder;
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
        return null;
    }

    private void writeAudioDataToFile(){
        byte buffer[] = new byte[mBufferSize];

        tempFilename = getNewFilename();
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(tempFilename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int read = 0;

        if(null != os){
            while(mIsRecording){
                read = mAudioRecord.read(buffer, 0, mBufferSize);

                if(AudioRecord.ERROR_INVALID_OPERATION != read){
                    try {
                        os.write(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Stops recording.
     * @throws SoundRecordingException
     */
    private void stop() throws SoundRecordingException {
        if (!mIsRecording) {
            throw new SoundRecordingException("Not started, can't be stopped.");
        }
        mIsRecording = false;

        mAudioRecord.stop();
        mAudioRecord.release();

        recordingThread = null;

        String newFilename = getNewFilename();

        copyWaveFile(tempFilename, newFilename);
        deleteTempFile();

        Toast.makeText(SoundRecorder.this.mAppContext, "Recording ended!",
                Toast.LENGTH_SHORT).show();
        SoundRecorder.this.sounds.add(new Sound(
                new SoundData.Builder().setLocalFilePath(newFilename).build()));
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
                AUDIO_RECORDER_FILE_EXT_WAV);
    }

    private MediaRecorder.OnErrorListener mErrorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Toast.makeText(mAppContext, "Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
        }
    };

    private void deleteTempFile() {
        File file = new File(tempFilename);

        file.delete();
        tempFilename = null;
    }

    private void copyWaveFile(String inFilename,String outFilename){
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        int channels = (mChannels == AudioFormat.CHANNEL_IN_MONO ? 1 : 2);
        int audioEncoding = (mAudioEncoding == AudioFormat.ENCODING_PCM_8BIT ? 8 : 16);
        long byteRate = audioEncoding * mSampleRate * channels/8;

        byte[] data = new byte[mBufferSize];

        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;

            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    mSampleRate, channels, byteRate);

            while(in.read(data) != -1){
                out.write(data);
            }

            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void WriteWaveFileHeader(
            FileOutputStream out, long totalAudioLen,
            long totalDataLen, long longSampleRate, int channels,
            long byteRate) throws IOException {

        byte[] header = new byte[44];

        header[0] = 'R';  // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';  // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;  // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8);  // block align
        header[33] = 0;
        header[34] = (byte)(mAudioEncoding == AudioFormat.ENCODING_PCM_8BIT ? 8 : 16);  // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }
}
