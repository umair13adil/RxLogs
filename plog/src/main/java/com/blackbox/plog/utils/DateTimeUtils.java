package com.blackbox.plog.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * The type Date time utils.
 */
public class DateTimeUtils {

    private static String TAG = DateTimeUtils.class.getSimpleName();

    private static String getFullDateTimeString(long timestamp) {
        Date date = new Date(timestamp);
        String dayNumberSuffix = getDayOfMonthSuffix(date.getDate());
        SimpleDateFormat f1 = new SimpleDateFormat("d'" + dayNumberSuffix + "' MMMM yyyy ;;:mm:ss", Locale.ENGLISH);
        return f1.format(date);
    }

    public static String getTimeFormatted(String timestampFormat) {
        String formatted = getFullDateTimeString(System.currentTimeMillis());
        try {
            if (timestampFormat != null) {
                Date date = new Date(System.currentTimeMillis());
                SimpleDateFormat f1 = new SimpleDateFormat(timestampFormat, Locale.ENGLISH);
                formatted = f1.format(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formatted;
    }

    private static String getDayOfMonthSuffix(int n) {
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

    public static String getFullDateTimeStringCompressed(long timestamp) {
        Date date = new Date(timestamp);
        String formatted = "" + System.currentTimeMillis();
        try {
            SimpleDateFormat f1 = new SimpleDateFormat("ddMMyyyy_kkmmss_a", Locale.ENGLISH);
            formatted = f1.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formatted;
    }
}
