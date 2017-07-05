package com.blackbox.plog

import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Waleed on 06/04/2017.
 */

class DateControl {

    val today: String
        get() {
            val currentTime = Date(System.currentTimeMillis())
            val sdf = SimpleDateFormat("ddMMyyyy", Locale.ENGLISH)
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

    val hour: String
        get() {
            val currentTime = Date(System.currentTimeMillis())
            val sdf = SimpleDateFormat("HH", Locale.ENGLISH)
            return sdf.format(currentTime)
        }

    val currentDate: String
        get() {
            val currentTime = Date(System.currentTimeMillis())
            val sdf = SimpleDateFormat("dd", Locale.ENGLISH)
            return sdf.format(currentTime)
        }

    companion object Factory {
        fun create(): DateControl = DateControl()
    }
}
