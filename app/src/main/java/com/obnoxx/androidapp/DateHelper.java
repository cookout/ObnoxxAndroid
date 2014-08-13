package com.obnoxx.androidapp;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Methods for serializing and deserializing dates.
 */
public class DateHelper {
    private static final String TAG = "DateHelper";
    private static final DateFormat DATE_TIME_FORMATTER =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    public static Date parse(String dateStr) {
        try {
            return DATE_TIME_FORMATTER.parse(dateStr);
        } catch (ParseException e) {
            Log.w(TAG, "Could not parse date: " + dateStr, e);
            return new Date();
        }
    }

    public static String format(Date date) {
        return DATE_TIME_FORMATTER.format(date);
    }
}
