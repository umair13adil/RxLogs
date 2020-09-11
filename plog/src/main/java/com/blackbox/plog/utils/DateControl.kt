package com.blackbox.plog.utils

import androidx.annotation.Keep
import com.blackbox.plog.pLogs.formatter.TimeStampFormat
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Umair Adil on 06/04/2017.
 */
@Keep
class DateControl {

    private val TAG = DateControl::class.java.simpleName

    val today: String
        get() {
            val currentTime = Date(System.currentTimeMillis())
            val sdf = SimpleDateFormat(TimeStampFormat.DATE_FORMAT_1, Locale.ENGLISH)
            return sdf.format(currentTime)
        }

    val lastWeek: String
        get() {
            val cal = Calendar.getInstance()
            cal.time = Date()
            cal.add(Calendar.DAY_OF_YEAR, -7)
            val date = cal.time
            val sdf = SimpleDateFormat("dd", Locale.ENGLISH)
            return sdf.format(date)
        }

    val lastDay: String
        get() {
            val cal = Calendar.getInstance()
            cal.time = Date()
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val date = cal.time
            val sdf = SimpleDateFormat("dd", Locale.ENGLISH)
            return sdf.format(date)
        }

    val hour: String
        get() {
            val currentTime = Date(System.currentTimeMillis())
            val sdf = SimpleDateFormat("HH", Locale.ENGLISH)
            return sdf.format(currentTime)
        }

    val time: String
        get() {
            val currentTime = Date(System.currentTimeMillis())
            val sdf = SimpleDateFormat("HH_mm", Locale.ENGLISH)
            return sdf.format(currentTime)
        }

    val currentDate: String
        get() {
            val currentTime = Date(System.currentTimeMillis())
            val sdf = SimpleDateFormat("dd", Locale.ENGLISH)
            return sdf.format(currentTime)
        }

    companion object {

        val instance = DateControl()
    }
}
