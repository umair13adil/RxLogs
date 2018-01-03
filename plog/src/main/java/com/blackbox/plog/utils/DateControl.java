package com.blackbox.plog.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * Created by Umair Adil on 06/04/2017.
 */

public class DateControl {

    private String TAG = DateControl.class.getSimpleName();

    private static final DateControl ourInstance = new DateControl();

    public static DateControl getInstance() {
        return ourInstance;
    }

    public String getToday() {
        Date currentTime = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy", Locale.ENGLISH);
        return sdf.format(currentTime);
    }

    public String getLastWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, -7);
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.ENGLISH);
        return sdf.format(date);
    }

    public String getLastDay() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.ENGLISH);
        return sdf.format(date);
    }

    public String getHour() {
        Date currentTime = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("HH", Locale.ENGLISH);
        return sdf.format(currentTime);
    }

    public String getCurrentDate() {
        Date currentTime = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.ENGLISH);
        return sdf.format(currentTime);
    }
}
