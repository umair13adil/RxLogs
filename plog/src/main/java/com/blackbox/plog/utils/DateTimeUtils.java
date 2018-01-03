package com.blackbox.plog.utils;

import android.text.TextUtils;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * The type Date time utils.
 */
public class DateTimeUtils {

    private static String TAG = DateTimeUtils.class.getSimpleName();
    private static final String DATE_FORMAT_DEFAULT = "MM/dd/yyyy";
    private static final String TIME_FORMAT_DEFAULT = "hh:mm:ss a";
    private static final String TIME_FORMAT_SHORT = "hh:mm";
    private static final String TIME_FORMAT_FULL = "MM:dd:yyyy hh:mm:ss a";
    private static final String TIME_FORMAT_READABLE = "dd MMMM yyyy hh:mm:ss a";

    public static String getFullDateString(int year, int monthOfYear, int dayOfMonth) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            Date date = sdf.parse(String.format("%2d/%2d/%4d", dayOfMonth, monthOfYear, year));

            String dayNumberSuffix = getDayOfMonthSuffix(dayOfMonth);
            SimpleDateFormat f1 = new SimpleDateFormat("d'" + dayNumberSuffix + "' MMM yyyy", Locale.ENGLISH);
            String formatted = f1.format(date);
            return formatted;
        } catch (ParseException e) {
            // handle exception here !
        }
        return String.format(DATE_FORMAT_DEFAULT, monthOfYear, dayOfMonth, year);
    }

    public static String getFullDateString(long timestamp) {
        Date date = new Date(timestamp);

        String dayNumberSuffix = getDayOfMonthSuffix(date.getDate());
        SimpleDateFormat f1 = new SimpleDateFormat("d'" + dayNumberSuffix + "' MMMM yyyy", Locale.ENGLISH);
        String formatted = f1.format(date);
        return formatted;
    }

    public static String getFullDateTimeString(long timestamp) {
        Date date = new Date(timestamp);

        String dayNumberSuffix = getDayOfMonthSuffix(date.getDate());
        SimpleDateFormat f1 = new SimpleDateFormat("d'" + dayNumberSuffix + "' MMMM yyyy hh:mm:ss a", Locale.ENGLISH);
        String formatted = f1.format(date);
        return formatted;
    }

    public static String getFullDateTimeStringCompressed(long timestamp) {
        Date date = new Date(timestamp);
        String formatted = "" + System.currentTimeMillis();
        try {
            String dayNumberSuffix = getDayOfMonthSuffix(date.getDate());
            SimpleDateFormat f1 = new SimpleDateFormat("ddMMyyyy_hhmmss_a", Locale.ENGLISH);
            formatted = f1.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formatted;
    }

    public static String getLogFileName(long timestamp) {
        Date date = new Date(timestamp);
        String formatted = "" + System.currentTimeMillis();
        try {
            String dayNumberSuffix = getDayOfMonthSuffix(date.getDate());
            SimpleDateFormat f1 = new SimpleDateFormat("d_MM_hh", Locale.ENGLISH);
            formatted = f1.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formatted;
    }

    public static String getDateString(int year, int monthOfYear, int dayOfMonth) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            Date date = sdf.parse(String.format("%2d/%2d/%4d", dayOfMonth, monthOfYear, year));

            SimpleDateFormat f1 = new SimpleDateFormat(DATE_FORMAT_DEFAULT, Locale.ENGLISH);
            String formatted = f1.format(date);
            return formatted;
        } catch (ParseException e) {
            // handle exception here !
        }
        return String.format(DATE_FORMAT_DEFAULT, monthOfYear, dayOfMonth, year);
    }

    public static String getDateString(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat f1 = new SimpleDateFormat(DATE_FORMAT_DEFAULT, Locale.ENGLISH);
        String formatted = f1.format(date);
        return formatted;
    }

    public static String getTimeString(long timestamp) {
        Date date1 = new Date(timestamp);
        SimpleDateFormat f1 = new SimpleDateFormat(TIME_FORMAT_DEFAULT, Locale.ENGLISH);
        String formatted = f1.format(date1);
        return formatted;
    }

    public static String getTimeStringShort(long timestamp) {
        Date date1 = new Date(timestamp);
        SimpleDateFormat f1 = new SimpleDateFormat(TIME_FORMAT_SHORT, Locale.ENGLISH);
        String formatted = f1.format(date1);
        return formatted;
    }

    public static String getTimeFormatted(long timestamp) {
        Date date1 = new Date(timestamp);
        SimpleDateFormat f1 = new SimpleDateFormat(TIME_FORMAT_FULL, Locale.ENGLISH);
        String formatted = f1.format(date1);
        return formatted;
    }

    public static String getTimeString(int hour, int minute) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            Date date = sdf.parse(String.format("%2s:%2s", hour, minute));

            SimpleDateFormat f1 = new SimpleDateFormat(TIME_FORMAT_DEFAULT, Locale.ENGLISH);
            String formatted = f1.format(date);
            return formatted;
        } catch (ParseException e) {
            // handle exception here !
        }
        return null;
    }

    public static long getTimestamp(int year, int month, int day) {
        try {
            String strDate = String.format("%2d-%2d-%4d", day, month, year);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            Date date = formatter.parse(strDate);
            return date.getTime();
        } catch (Exception e) {
        }
        return 0;
    }

    public static long getTimestamp(String d) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT_DEFAULT, Locale.ENGLISH);
            Date date = formatter.parse(d);
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long getTimestamp(int year, int month, int day, int hour, int minute) {
        try {
            String strDate = String.format("%2d-%2d-%4d %2d:%2d", day, month, year, hour, minute);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH);
            Date date = formatter.parse(strDate);
            return date.getTime();
        } catch (Exception e) {
        }
        return 0;
    }

    public static String getDayOfMonthSuffix(int n) {
        if (n < 1 || n > 31) {
            throw new IllegalArgumentException("Illegal day of month");
        }
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public static long getUTCTimeStamp(long timestamp) {
        Date netDate = null;
        DateFormat df = DateFormat.getTimeInstance();
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        long time_utc = new Date(timestamp).getTime();
        Log.i(TAG, "Time: " + time_utc);
        return time_utc / 1000L;
    }

    public static String formatDate(String date) {
        String dateFormatted = "";
        SimpleDateFormat spf = new SimpleDateFormat("dd-MM-yyy", Locale.ENGLISH);
        Date newDate = null;
        try {
            newDate = spf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf = new SimpleDateFormat("ddMMyyyy", Locale.ENGLISH);
        dateFormatted = spf.format(newDate);
        return dateFormatted;
    }

    public static String formatComplaintDate(String date) {
        String dateFormatted = "";
        SimpleDateFormat spf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        Date newDate = null;
        try {
            newDate = spf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return getFullDateString(newDate.getTime());
    }


    public static String getTimeDifference(String d1, String d2) {

        long elapsedHours = 0;
        long elapsedMinutes = 0;
        long elapsedSeconds = 0;

        if (!TextUtils.isEmpty(d1) && !d1.contains("-") && !TextUtils.isEmpty(d2) && !d2.contains("-")) {
            try {
                //milliseconds
                long different = getTimestamp(d2) - getTimestamp(d1);

                long secondsInMilli = 1000;
                long minutesInMilli = secondsInMilli * 60;
                long hoursInMilli = minutesInMilli * 60;

                elapsedHours = different / hoursInMilli;
                different = different % hoursInMilli;

                elapsedMinutes = different / minutesInMilli;

                elapsedSeconds = different / secondsInMilli;


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return elapsedHours + " hours " + elapsedMinutes + " mins "+ elapsedSeconds + " secs";
    }
}
