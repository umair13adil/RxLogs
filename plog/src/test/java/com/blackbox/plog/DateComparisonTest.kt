package com.blackbox.plog

import java.text.SimpleDateFormat
import java.util.*
import kotlin.test.Test

class DateComparisonTest {

    @Test
    fun testDateComparison() {
        val sdf = SimpleDateFormat("ddMMyyyy", Locale.ENGLISH)

        // Today: October 7, 2025
        val today = sdf.parse("07102025")
        println("Today: ${sdf.format(today)} (${today?.time})")

        // Calculate cutoff: today - 7 days = September 30, 2025
        val cal = Calendar.getInstance()
        cal.time = today ?: Date()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.add(Calendar.DAY_OF_YEAR, -7)
        val cutoffDate = cal.time
        println("Cutoff: ${sdf.format(cutoffDate)} (${cutoffDate.time})")

        // Test various dates
        val testDates = listOf(
            "06102025",  // Oct 6 - should keep
            "30092025",  // Sep 30 - should keep (boundary)
            "29092025",  // Sep 29 - should delete
            "20092025",  // Sep 20 - should delete
            "08092025"   // Sep 8 - should delete
        )

        testDates.forEach { dateStr ->
            val date = sdf.parse(dateStr)
            val shouldDelete = date?.before(cutoffDate) == true
            println("Date: $dateStr (${date?.time}) - before cutoff: $shouldDelete - Action: ${if (shouldDelete) "DELETE" else "KEEP"}")
        }
    }
}

