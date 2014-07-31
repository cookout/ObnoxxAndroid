package com.obnoxx.androidapp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Data object representing a user, who may or may not be the current user.
 * Ultimately the client will have a database-backed cache of all the Users
 * he has shared sounds with, and those Users will be represented in memory
 * with this object.
 */
public class User {
    private static final String TAG = "User";

    private static final String ID_STR = "id";
    private static final String EMAIL_STR = "email";
    private static final String NAME_STR = "name";
    private static final String PHONE_NUMBER_STR = "phoneNumber";
    private static final String FACEBOOK_USER_ID_STR = "fbUserId";
    private static final String IMAGE_URL_STR = "imageUrl";
    private static final String CREATE_DATE_TIME_STR = "createDateTime";
    public static final DateFormat DATE_TIME_FORMATTER =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final String mId;
    private final String mEmail;
    private final String mName;
    private final String mPhoneNumber;
    private final String mFacebookUserId;
    private final String mImageUrl;
    private Date mCreateDate = null;

    public User(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        mId = jsonObject.optString("id");
        mEmail = jsonObject.optString("email");
        mName = jsonObject.optString("name");
        mPhoneNumber = jsonObject.optString("phoneNumber");
        mFacebookUserId = jsonObject.optString("facebookUserId");
        mImageUrl = jsonObject.optString("imageUrl");

        try {
            mCreateDate = DATE_TIME_FORMATTER.parse(jsonObject.optString("createDateTime"));
        } catch (ParseException e) {
            Log.w(TAG, "Could not parse date: " + jsonObject.optString("createDateTime"), e);
        }
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject o = new JSONObject();
        o.put(ID_STR, mId);
        o.put(NAME_STR, mName);
        o.put(PHONE_NUMBER_STR, mPhoneNumber);
        o.put(CREATE_DATE_TIME_STR, DATE_TIME_FORMATTER.format(mCreateDate));

        if (mEmail != null) {
            o.put(EMAIL_STR, mEmail);
        }

        if (mImageUrl != null) {
            o.put(IMAGE_URL_STR, mImageUrl);
        }

        if (mFacebookUserId != null) {
            o.put(FACEBOOK_USER_ID_STR, mFacebookUserId);
        }

        return o;
    }

    public String getId() {
        return mId;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getName() {
        return mName;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public String getFacebookUserId() {
        return mFacebookUserId;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public Date getCreateDate() {
        return mCreateDate;
    }
}
