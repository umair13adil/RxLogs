package com.blackbox.plog.pLogs

import com.blackbox.plog.pLogs.config.LogsConfig
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PLogRetentionTest {

    private val tempRoot: File = createTempDir(prefix = "plog_retention_test_")

    @After
    fun tearDown() {
        tempRoot.deleteRecursively()
    }

    @Test
    fun clearLogsOlderThan_keeps_recent_and_deletes_older() {
        val sdf = SimpleDateFormat(com.blackbox.plog.pLogs.formatter.TimeStampFormat.DATE_FORMAT_1, Locale.ENGLISH)

        // Create Logs root
        val logsRoot = File(tempRoot, "Logs")
        logsRoot.mkdirs()

        // Helper to format date offset by days
        fun dayString(offset: Int): String {
            val cal = Calendar.getInstance()
            cal.time = Date()
            cal.add(Calendar.DAY_OF_YEAR, offset)
            return sdf.format(cal.time)
        }

        // Create dated folders: today, -1, -2, -3
        val d0 = File(logsRoot, dayString(0))
        val d1 = File(logsRoot, dayString(-1))
        val d2 = File(logsRoot, dayString(-2))
        val d3 = File(logsRoot, dayString(-3))

        listOf(d0, d1, d2, d3).forEach { it.mkdirs() }

        // Point PLog to temp save path by applying config
        val config = LogsConfig(savePath = tempRoot.absolutePath, exportPath = tempRoot.absolutePath)
        PLog.forceWriteLogsConfig(config)

        // Retention 2 days: cutoff = today - 2; anything before that (<= -3) should be deleted
        PLog.clearLogsOlderThan(2)

        assertTrue("today should remain", d0.exists())
        assertTrue("today-1 should remain", d1.exists())
        assertTrue("today-2 should remain (boundary)", d2.exists())
        assertFalse("today-3 should be deleted (older than cutoff)", d3.exists())
    }
}


