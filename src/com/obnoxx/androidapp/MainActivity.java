package com.obnoxx.androidapp;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {
    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private MediaRecorder recorder = null;
    private int currentFormat = 0;
    private int outputFormats[] = {
            MediaRecorder.OutputFormat.MPEG_4,
            MediaRecorder.OutputFormat.THREE_GPP
    };
    private String fileExts[] = { AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setButtonHandlers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setButtonHandlers() {
        ((Button) findViewById(R.id.btnRecord)).setOnClickListener(onClickListener);
        ((Button) findViewById(R.id.btnPlay)).setOnClickListener(onClickListener);
        ((Button) findViewById(R.id.btnSend)).setOnClickListener(onClickListener);
    }
    
    private void enableButton(int id, boolean isEnable) {
        ((Button) findViewById(id)).setEnabled(isEnable);
    }
    
    private String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
        return (file.getAbsolutePath() + "/current" + fileExts[currentFormat]);
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(outputFormats[currentFormat]);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(getFilename());
        recorder.setOnErrorListener(errorListener);
        recorder.setOnInfoListener(infoListener);
        try {
            recorder.prepare();
            recorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        Toast.makeText(MainActivity.this, "Stop Recording", Toast.LENGTH_SHORT).show();
        if (null != recorder) {
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
        }
    }

    private void play() {
        Toast.makeText(MainActivity.this, "Playing...", Toast.LENGTH_SHORT).show();
        MediaPlayer mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(getFilename());
            mMediaPlayer.prepare();
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "Error Playing...", Toast.LENGTH_SHORT).show();
        }
        mMediaPlayer.start();
    }
    
    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
        Toast.makeText(MainActivity.this, "Error: " + what + ", " + extra,
                Toast.LENGTH_SHORT).show();
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
        Toast.makeText(MainActivity.this, "Warning: " + what + ", " + extra,
                Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRecord:
                Toast.makeText(MainActivity.this, "Start Recording", Toast.LENGTH_SHORT).show();
                startRecording();

                // Stop recording in 5s
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        enableButton(R.id.btnPlay, true);
                        stopRecording();
                    }
                }, 5000);
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

    private void send() {
        SendHttpRequestTask t = new SendHttpRequestTask();
        //String[] params = new String[0];
        t.execute();
    }

    private class SendHttpRequestTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection connection = null;
            DataOutputStream outputStream = null;
            DataInputStream inputStream = null;
            String urlServer = "http://www.obnoxx.co/addSound";
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary =  "*****";
            String response;
             
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            String serverResponseMessage = null;
            
            try {
                FileInputStream fileInputStream = new FileInputStream(new File(getFilename()));
                URL url = new URL(urlServer);

                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "multipart/form-data;boundary=" + boundary);
             
                outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.write("Content-Type: text/plain\r\n".getBytes());
                outputStream.write("Content-Disposition: form-data; name=\"sessionId\"\r\n".getBytes());
                outputStream.write("\r\n9tvQvzoXH1dPSPBCkaCRHZ0se_Cjo8TYBKOGgdN0wRym0vbD1fwN4lItaAmPFAnG\r\n".getBytes());
                
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.write("Content-Type: text/plain\r\n".getBytes());
                outputStream.write("Content-Disposition: form-data; name=\"phoneNumber\"\r\n".getBytes());
                outputStream.write("\r\n4153163345\r\n".getBytes());

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.write("Content-Disposition: form-data; name=\"soundFile\"; filename=\"test\"\r\n".getBytes());
                outputStream.write("Content-Type: application/octet-stream\r\n".getBytes());
                outputStream.write("Content-Transfer-Encoding: binary\r\n".getBytes());
                outputStream.write("\r\n".getBytes());

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
             
                // Read file
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
             
                while (bytesRead > 0) {
                    outputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
             
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseMessage = connection.getResponseMessage();
             
                fileInputStream.close();
                outputStream.flush();
                outputStream.close();
            } catch (IOException ex) {
                // TODO(jon): Implement retry / user notification of error (maybe a Toast?).
                throw new RuntimeException(ex);
            }
            return (serverResponseMessage != null) ? serverResponseMessage : "error";
        }
        
        @Override
        protected void onPostExecute(String data) {            
            // Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();;
        }
    } // SendHttpRequestTask

} // MainActivity

