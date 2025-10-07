package com.blackbox.library.plog

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import com.blackbox.plog.utils.appendToFile
import com.blackbox.plog.utils.setupFilePaths
import com.mooveit.library.Fakeit
import java.util.*

class AutoClearLogsTester : AppCompatActivity() {

    private val TAG = "AutClearLogsTester"
    private var eventsTV: AppCompatTextView? = null
    private var clearBtn: AppCompatButton? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hourly_logs_test) // Reuse layout for simplicity

        eventsTV = findViewById(R.id.events)
        // Fix: Use AppCompatButton for compatibility and ensure correct reference
        clearBtn = findViewById(R.id.runAutoClearBtn)

        Fakeit.initWithLocale(Locale.ENGLISH)

        writeLogsFor30Days()

        clearBtn?.setOnClickListener {
            // Use Triggers.shouldClearLogs() with forceRun=true to bypass time check
            val deletedFiles = com.blackbox.plog.pLogs.operations.Triggers.shouldClearLogs(forceRun = true)

            // Display the deleted files
            val sb = StringBuilder()
            sb.append("Auto-clear logs triggered!\n\n")
            sb.append("===== DELETION SUMMARY =====\n")
            sb.append("Total files/folders deleted: ${deletedFiles.size}\n\n")

            if (deletedFiles.isNotEmpty()) {
                sb.append("Deleted items:\n")
                deletedFiles.forEachIndexed { index, path ->
                    // Extract just the filename/folder name for cleaner display
                    val name = path.substringAfterLast('/')
                    sb.append("${index + 1}. $name\n")
                }
            } else {
                sb.append("No files were deleted.\n")
                sb.append("(All logs are within retention period)")
            }

            eventsTV?.text = sb.toString()
        }
    }

    private fun writeLogsFor30Days() {
        val calendar = Calendar.getInstance()
        // Start from 29 days ago (so we include today, making it 30 days total)
        calendar.add(Calendar.DATE, -29)

        val logsPerDay = 5 // Adjust as needed
        val sb = StringBuilder()
        val dateFormat = java.text.SimpleDateFormat("ddMMyyyy", java.util.Locale.ENGLISH)

        sb.append("Writing logs for the last 30 days...\n\n")

        for (i in 0 until 30) {
            val dateStr = dateFormat.format(calendar.time)
            // Use the date as a custom folder name for test logs
            val filePath = setupFilePaths(fileName = "_test_$dateStr", isPLog = true, folderDate = dateStr)
            for (j in 0 until logsPerDay) {
                val title = Fakeit.ancient().titan()
                val message = Fakeit.friends().quote()
                val logEntry = "[${dateStr}] $title: $message\n"
                appendToFile(filePath, logEntry)
            }
            sb.append("Logs written for: ").append(dateStr).append("\n")
            calendar.add(Calendar.DATE, 1)
        }
        eventsTV?.text = sb.toString()
    }
}
