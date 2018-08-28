package com.blackbox.plog.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * The type Date time utils.
 */
object DateTimeUtils {

    private val TAG = DateTimeUtils::class.java.simpleName

    private fun getFullDateTimeString(timestamp: Long): String {
        val date = Date(timestamp)
        val dayNumberSuffix = getDayOfMonthSuffix(date.date)
        val f1 = SimpleDateFormat("d'$dayNumberSuffix' MMMM yyyy ;;:mm:ss", Locale.ENGLISH)
        return f1.format(date)
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

    private fun getDayOfMonthSuffix(n: Int): String {
        if (n < 1 || n > 31) {
            throw IllegalArgumentException("Illegal day of month")
        }
        if (n >= 11 && n <= 13) {
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
            val f1 = SimpleDateFormat("ddMMyyyy_kkmmss_a", Locale.ENGLISH)
            formatted = f1.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return formatted
    }
}
