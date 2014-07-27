package com.example.record;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.os.Build;



public class MainActivity extends ActionBarActivity {
	private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
	private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
	private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
	private MediaRecorder recorder = null;
	private int currentFormat = 0;
	private int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP };
	private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP };
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        setButtonHandlers();
        enableButton(R.id.btnPlay, false);
        //enableButton(R.id.btnSend, false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
    
    private void setButtonHandlers() {
        ((Button) findViewById(R.id.btnRecord)).setOnClickListener(btnClick);
        ((Button) findViewById(R.id.btnPlay)).setOnClickListener(btnClick);
        ((Button) findViewById(R.id.btnSend)).setOnClickListener(btnClick);
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
        return (file.getAbsolutePath() + "/current" + file_exts[currentFormat]); // + System.currentTimeMillis()
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(output_formats[currentFormat]);
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
    
    private void send() {
		SendHttpRequestTask t = new SendHttpRequestTask();
		//String[] params = new String[0];
		t.execute();
    }
    
    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Toast.makeText(MainActivity.this, "Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            Toast.makeText(MainActivity.this, "Warning: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnRecord: {
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
                }
                case R.id.btnPlay: {
                	play();
                    break;
                }
                case R.id.btnSend: {
                    send();
                    break;
                }
            }
        }
    };
    
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
        	int maxBufferSize = 1*1024*1024;
        	int serverResponseCode = -1;
        	String serverResponseMessage = null;
        	
        	try
        	{
        	    FileInputStream fileInputStream = new FileInputStream(new File(getFilename()) );
        	    URL url = new URL(urlServer);
        	    connection = (HttpURLConnection) url.openConnection();
        	 
        	    // Allow Inputs &amp; Outputs.
        	    connection.setDoInput(true);
        	    connection.setDoOutput(true);
        	    connection.setUseCaches(false);
        	    connection.setRequestProperty("Host", "www.obnoxx.co");
        	    connection.setRequestProperty("User-Agent", "AndroidApp");
        	 
        	    // Set HTTP method to POST.
        	    connection.setRequestMethod("POST");
        	    
        	    connection.setRequestProperty("Connection", "Keep-Alive");
        	    connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
        	 
        	    outputStream = new DataOutputStream( connection.getOutputStream() );
        	    //serverResponseMessage = connection.getResponseMessage();
        	    outputStream.writeBytes(twoHyphens + boundary + lineEnd);
        	    outputStream.write( "Content-Type: text/plain\r\n".getBytes());
        	    outputStream.write( ("Content-Disposition: form-data; name=\"sessionId\"\r\n").getBytes());;
        	    outputStream.write( ("\r\n9tvQvzoXH1dPSPBCkaCRHZ0se_Cjo8TYBKOGgdN0wRym0vbD1fwN4lItaAmPFAnG\r\n").getBytes());
        	    
        	    outputStream.writeBytes(twoHyphens + boundary + lineEnd);
        	    outputStream.write( "Content-Type: text/plain\r\n".getBytes());
        	    outputStream.write( ("Content-Disposition: form-data; name=\"phoneNumber\"\r\n").getBytes());;
        	    outputStream.write( ("\r\n4153163345\r\n").getBytes());
        	    
        	    outputStream.writeBytes(twoHyphens + boundary + lineEnd);
        	    outputStream.write( ("Content-Disposition: form-data; name=\"soundFile\"; filename=\"test\"\r\n").getBytes());
        	    outputStream.write( ("Content-Type: application/octet-stream\r\n").getBytes());
        	    outputStream.write( ("Content-Transfer-Encoding: binary\r\n").getBytes());
        	    outputStream.write("\r\n".getBytes());
        	    //outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + getFilename() +"\"" + lineEnd);
        	    //outputStream.writeBytes(lineEnd);
        	 
        	    bytesAvailable = fileInputStream.available();
        	    bufferSize = Math.min(bytesAvailable, maxBufferSize);
        	    buffer = new byte[bufferSize];
        	 
        	    // Read file
        	    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        	 
        	    while (bytesRead > 0)
        	    {
        	        outputStream.write(buffer, 0, bufferSize);
        	        bytesAvailable = fileInputStream.available();
        	        bufferSize = Math.min(bytesAvailable, maxBufferSize);
        	        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        	    }
        	 
        	    outputStream.writeBytes(lineEnd);
        	    outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
        	    
        	 
        	    // Responses from the server (code and message)
        	    serverResponseCode = connection.getResponseCode();
        	    serverResponseMessage = connection.getResponseMessage();
        	 
        	    fileInputStream.close();
        	    outputStream.flush();
        	    outputStream.close();
        	}
        	catch (Exception ex)
        	{
        	    //Exception handling
        		int cat = 2+2;
        	}
        	if (serverResponseMessage != null)
        		return serverResponseMessage;
        	return "error";
        	/*
        	
        	String BOUNDRY = "==================================";
            HttpURLConnection conn = null;

            try {

                // These strings are sent in the request body. They provide information about the file being uploaded
                String contentDisposition = "Content-Disposition: form-data; name=\"userfile\"; filename=\"" + getFilename() + "\"";
                String contentType = "Content-Type: application/octet-stream";

                // This is the standard format for a multipart request
                StringBuffer requestBody = new StringBuffer();
                requestBody.append("--");
                requestBody.append(BOUNDRY);
                requestBody.append('\n');
                requestBody.append(contentDisposition);
                requestBody.append('\n');
                requestBody.append(contentType);
                requestBody.append('\n');
                requestBody.append('\n');
                //requestBody.append(new String(Util.getBytesFromFile(file)));
                // Make a connect to the server
                URL url = new URL(urlServer);
                conn = (HttpURLConnection) url.openConnection();

                // Put the authentication details in the request
                //if (username != null) {
                //    String usernamePassword = username + ":" + password;
                //    String encodedUsernamePassword = Base64.encodeBytes(usernamePassword.getBytes());
                //    conn.setRequestProperty ("Authorization", "Basic " + encodedUsernamePassword);
                //}/

                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDRY);

                // Send the body
                DataOutputStream dataOS = new DataOutputStream(conn.getOutputStream());
                dataOS.writeBytes(requestBody.toString());
                dataOS.flush();
                
                
                
                FileInputStream fileInputStream = new FileInputStream(new File(getFilename()) );
                bytesAvailable = fileInputStream.available();
        	    bufferSize = Math.min(bytesAvailable, maxBufferSize);
        	    buffer = new byte[bufferSize];
        	 
        	    // Read file
        	    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        	 
        	    while (bytesRead > 0)
        	    {
        	        dataOS.write(buffer, 0, bufferSize);
        	        bytesAvailable = fileInputStream.available();
        	        bufferSize = Math.min(bytesAvailable, maxBufferSize);
        	        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        	    }
                dataOS.writeBytes("--");
                dataOS.writeBytes(BOUNDRY);
                dataOS.writeBytes("--");
                dataOS.flush();


                dataOS.close();

                // Ensure we got the HTTP 200 response code
                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    response = String.format("Received the response code %d from the URL %s", responseCode, url);
                } else {

	                // Read the response
	                InputStream is = conn.getInputStream();
	                ByteArrayOutputStream baos = new ByteArrayOutputStream();
	                byte[] bytes = new byte[1024];
	                //int bytesRead;
	                while((bytesRead = is.read(bytes)) != -1) {
	                    baos.write(bytes, 0, bytesRead);
	                }
	                byte[] bytesReceived = baos.toByteArray();
	                baos.close();
	
	                is.close();
	                response = new String(bytesReceived);
                }

            } catch (IOException ex) {
            	response = "ioexception";
            } catch (Exception ex) {
            	response = "other exception";
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return response;
            */
    	}
    	
    	@Override
    	protected void onPostExecute(String data) {			
    		//Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();;
    	}
    } // SendHttpRequestTask
    	

} // MainActivity

