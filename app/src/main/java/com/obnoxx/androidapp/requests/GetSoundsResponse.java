package com.obnoxx.androidapp.requests;

import com.obnoxx.androidapp.data.Sound;
import com.obnoxx.androidapp.data.SoundData;
import com.obnoxx.androidapp.data.SoundDelivery;
import com.obnoxx.androidapp.data.SoundDeliveryData;
import com.obnoxx.androidapp.data.User;
import com.obnoxx.androidapp.data.UserData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetSoundsResponse {
    private int mStatusCode = 0;
    private List<Sound> mSounds = null;
    private List<SoundDelivery> mSoundDeliveries = null;
    private List<User> mUsers = null;

    public GetSoundsResponse(String json) {
        mStatusCode = 200;

        try {
            JSONObject jsonObject = new JSONObject(json);

            // Handle sounds.
            mSounds = new ArrayList<Sound>();
            JSONArray soundJsons = jsonObject.getJSONArray("sounds");
            for (int i = 0; i < soundJsons.length(); i++) {
                mSounds.add(new Sound(new SoundData(soundJsons.getJSONObject(i))));
            }

            // Handle sounds deliveries.
            mSoundDeliveries = new ArrayList<SoundDelivery>();
            JSONArray soundDeliveryJsons = jsonObject.getJSONArray("soundDeliveries");
            for (int i = 0; i < soundDeliveryJsons.length(); i++) {
                mSoundDeliveries.add(new SoundDelivery(new SoundDeliveryData(
                        soundDeliveryJsons.getJSONObject(i))));
            }

            // Handle users.
            mUsers = new ArrayList<User>();
            JSONArray userJsons = jsonObject.getJSONArray("users");
            for (int i = 0; i < userJsons.length(); i++) {
                mUsers.add(new User(new UserData(userJsons.getJSONObject(i))));
            }

        } catch (JSONException e) {
            mStatusCode = 600;
            return;
        }
    }

    public GetSoundsResponse(int statusCode) {
        mStatusCode = statusCode;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    public List<User> getUsers() {
        return mUsers;
    }

    public List<SoundDelivery> getSoundDeliveries() {
        return mSoundDeliveries;
    }

    public List<Sound> getSounds() {
        return mSounds;
    }
}