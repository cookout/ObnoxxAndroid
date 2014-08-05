package com.obnoxx.androidapp.requests;

import android.content.Context;
import android.os.AsyncTask;

import com.obnoxx.androidapp.CurrentUser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;

public class GetSoundsRequest extends AsyncTask<Void, Void, GetSoundsResponse> {
    private static final String BASE_URL = "http://www.obnoxx.co/getSounds";

    private final Context mContext;

    public GetSoundsRequest(Context context) {
        mContext = context;
    }

    @Override
    protected GetSoundsResponse doInBackground(Void... params) {
        ContentType foo = ContentType.APPLICATION_ATOM_XML;

        HttpClient client = new DefaultHttpClient();
        HttpGet get = null;
        try {
            get = new HttpGet(new URIBuilder(BASE_URL)
                    .addParameter("sessionId", CurrentUser.getSessionId(mContext))
                    .build());
        } catch (URISyntaxException e) {
            return new GetSoundsResponse(600);
        }

        HttpResponse response;
        try {
            response = client.execute(get);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            if (responseString.startsWith("&&&PREFIX&&&")) {
                responseString = responseString.substring("&&&PREFIX&&&".length());
            }
            return (response.getStatusLine().getStatusCode() == 200) ?
                    new GetSoundsResponse(responseString) :
                    new GetSoundsResponse(response.getStatusLine().getStatusCode());
        } catch (IOException e) {
            return new GetSoundsResponse(500);
        }
    }
}
