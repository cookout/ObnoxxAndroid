package com.obnoxx.androidapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendHttpRequestTask extends AsyncTask<Void, Void, String> {
    private final Context mContext;
    private final String mFileName;
    private final int mCurrentFormat; // TODO(jonemerson): Figure out what we're trying to do here.
    private final String mPhoneNumber;

    public SendHttpRequestTask(Context context, String fileName, int currentFormat,
            String phoneNumber) {
        mContext = context;
        mFileName = fileName;
        mCurrentFormat = currentFormat;
        mPhoneNumber = phoneNumber;
    }

    @Override
    protected String doInBackground(Void... params) {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        DataInputStream inputStream = null;
        String urlServer = "http://www.obnoxx.co/addSound";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        String serverResponseMessage = null;

        try {
            FileInputStream fileInputStream = new FileInputStream(new File(mFileName));
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
            outputStream.write(("\r\n" + mPhoneNumber + "\r\n").getBytes());

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
        Toast.makeText(mContext, data, Toast.LENGTH_SHORT).show();;
    }
}
