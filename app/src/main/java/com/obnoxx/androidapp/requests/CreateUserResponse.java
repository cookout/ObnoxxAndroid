package com.obnoxx.androidapp.requests;

import com.obnoxx.androidapp.data.User;
import com.obnoxx.androidapp.data.UserData;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateUserResponse {
    private int mStatusCode = 0;
    private String mSessionId = null;
    private User mUser = null;

    public CreateUserResponse(String json) {
        mStatusCode = 200;

        try {
            JSONObject jsonObject = new JSONObject(json);
            mSessionId = jsonObject.optString("sessionId");
            mUser = jsonObject.has("user") ?
                    new User(new UserData(jsonObject.getString("user"))) : null;
        } catch (JSONException e) {
            mStatusCode = 600;
            return;
        }
    }

    public CreateUserResponse(int statusCode) {
        mStatusCode = statusCode;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    public String getSessionId() {
        return mSessionId;
    }

    public User getUser() {
        return mUser;
    }
}