package com.obnoxx.androidapp.requests;

import com.obnoxx.androidapp.data.UserData;

import org.json.JSONException;
import org.json.JSONObject;

public class VerifyPhoneNumberResponse {
    private int mStatusCode = 0;
    private String mTemporaryUserCode = null;
    private String mSessionId = null;
    private UserData mUserData = null;

    public VerifyPhoneNumberResponse(String json) {
        mStatusCode = 200;

        try {
            JSONObject jsonObject = new JSONObject(json);
            mTemporaryUserCode = jsonObject.optString("temporaryUserCode");
            mSessionId = jsonObject.optString("sessionId");
            mUserData = jsonObject.has("user") ? new UserData(jsonObject.optString("user")) : null;
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

    public UserData getUser() {
        return mUserData;
    }
}