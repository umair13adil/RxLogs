package com.blackbox.plog.utils

import androidx.annotation.Keep
import com.blackbox.plog.pLogs.formatter.TimeStampFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * The type Date time utils.
 */
@Keep
object DateTimeUtils {

    private val TAG = DateTimeUtils::class.java.simpleName

    internal fun getFullDateTimeString(timestamp: Long): String {
        val date = Date(timestamp)
        val dayNumberSuffix = getDayOfMonthSuffix(date.date)
        val f1 = SimpleDateFormat("d'$dayNumberSuffix' MMMM yyyy hh:mm:ss a", Locale.ENGLISH)
        return f1.format(date)
    }

    fun getHourlyFolderName(currentTimeLong: Long): String {
        val currentTime = Date(currentTimeLong)
        val sdf1 = SimpleDateFormat(TimeStampFormat.DATE_FORMAT_1, Locale.ENGLISH)
        val day = sdf1.format(currentTime)
        val sdf2 = SimpleDateFormat("HH", Locale.ENGLISH)
        val hour = sdf2.format(currentTime)
        return "$day$hour"
    }

    fun getTimeFormatted(timestampFormat: String?): String {
        var formatted = getFullDateTimeString(System.currentTimeMillis())
        try {
            if (timestampFormat != null) {
                val date = Date(System.currentTimeMillis())
                val f1 = SimpleDateFormat(timestampFormat, Locale.ENGLISH)
                formatted = f1.format(date)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return formatted
    }

    fun getTimeFormatted(): String {
        return getFullDateTimeString(System.currentTimeMillis())
    }

    private fun getDayOfMonthSuffix(n: Int): String {
        if (n < 1 || n > 31) {
            throw IllegalArgumentException("Illegal day of month")
        }
        if (n in 11..13) {
            return "th"
        }
        when (n % 10) {
            1 -> return "st"
            2 -> return "nd"
            3 -> return "rd"
            else -> return "th"
        }
    }

    fun getFullDateTimeStringCompressed(timestamp: Long): String {
        val date = Date(timestamp)
        var formatted = "" + System.currentTimeMillis()
        try {
            val f1 = SimpleDateFormat(TimeStampFormat.TIME_FORMAT_FULL_JOINED, Locale.ENGLISH)
            formatted = f1.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return formatted
    }

    fun getLastHourTime(): String {
        val someDate = Calendar.getInstance()
        someDate.add(Calendar.HOUR_OF_DAY, -1)
        val date = someDate.time
        val dateFormat = SimpleDateFormat(TimeStampFormat.TIME_FORMAT_SIMPLE, Locale.ENGLISH)
        return dateFormat.format(date)
    }

    private fun getPreviousWeekDate(): Date {
        val someDate = GregorianCalendar.getInstance()
        someDate.add(Calendar.DAY_OF_YEAR, -7)
        return someDate.time
    }

    fun getDatesBetween(): List<String> {
        val datesInRange = ArrayList<String>()
        val calendar = GregorianCalendar()
        calendar.time = getPreviousWeekDate()

        val endCalendar = GregorianCalendar()
        endCalendar.time = Date()
        endCalendar.add(Calendar.DATE, 1)

        while (calendar.before(endCalendar)) {
            val result = calendar.time
            val dateFormat = SimpleDateFormat(TimeStampFormat.DATE_FORMAT_1, Locale.ENGLISH)
            val date = dateFormat.format(result)
            datesInRange.add(date)
            calendar.add(Calendar.DATE, 1)
        }

        return datesInRange
    }
}
