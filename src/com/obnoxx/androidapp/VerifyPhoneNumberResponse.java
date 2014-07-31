package com.obnoxx.androidapp;

import org.json.JSONException;
import org.json.JSONObject;

public class VerifyPhoneNumberResponse {
    private int mStatusCode = 0;
    private String mTemporaryUserCode = null;
    private String mSessionId = null;
    private User mUser = null;

    public VerifyPhoneNumberResponse(String json) {
        mStatusCode = 200;

        try {
            JSONObject jsonObject = new JSONObject(json);
            mTemporaryUserCode = jsonObject.optString("temporaryUserCode");
            mSessionId = jsonObject.optString("sessionId");
            mUser = jsonObject.has("user") ? new User(jsonObject.optString("user")) : null;
        } catch (JSONException e) {
            mStatusCode = 600;
            return;
        }
    }

    public VerifyPhoneNumberResponse(int statusCode) {
        mStatusCode = statusCode;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    public String getTemporaryUserCode() {
        return mTemporaryUserCode;
    }

    public String getSessionId() {
        return mSessionId;
    }

    public User getUser() {
        return mUser;
    }
}