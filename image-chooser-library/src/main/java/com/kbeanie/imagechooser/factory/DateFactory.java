package com.kbeanie.imagechooser.factory;

import android.util.Log;

import java.util.Calendar;

/**
 * Created by vervik on 9/27/15.
 */
public class DateFactory {

    static String TAG = DateFactory.class.getSimpleName();

    private Long timeInMillis;


    private DateFactory() {
        // private
    }

    public long getTimeInMillis() {
        if(timeInMillis != null) {
            Log.d(TAG, "Time set. Is: "+timeInMillis);
            return timeInMillis;
        }
        return Calendar.getInstance().getTimeInMillis();
    }

    public void setTimeInMillis(long timeInMillis) {
        Log.d(TAG, "Setting time. Is: "+timeInMillis);
        this.timeInMillis = timeInMillis;
    }

    public void reset() {
        Log.d(TAG, "We reset time");
        this.timeInMillis = null;
    }

    private static DateFactory instance;

    public static DateFactory getInstance() {
        if(instance == null) {
            instance = new DateFactory();
        }
        return instance;
    }
}
