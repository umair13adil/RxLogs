package com.blackbox.library.plog

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.exporter.ExportType
import com.blackbox.plog.pLogs.models.LogLevel
import com.blackbox.plog.pLogs.models.LogType
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val TAG: String = MainActivity::class.java.simpleName
    var PERMISSION_CODE = 9234
    var ENCRYPTION_KEY = "23233526436245232364264262343243"
    var encryptLogs = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Check read write permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_CODE)
            return
        }

        //If permission granted
        setupLoggerControls()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_CODE) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                PLog.logThis(TAG, "onRequestPermissionsResult", "Permissions Granted!", LogLevel.INFO)

                //If permission granted
                setupLoggerControls()

            } else {
                PLog.logThis(TAG, "onRequestPermissionsResult", "Permissions Not Granted!", LogLevel.WARNING)
            }

        }
    }

    private fun setupLoggerControls() {

        //This will get 'DataLogger' object for predefined type in ConfigFile.
        val locationsLog = PLog.getLoggerFor(LogType.Location.type)
        val notificationsLog = PLog.getLoggerFor(LogType.Notification.type)
        val deliveriesLog = PLog.getLoggerFor("Deliveries")

        //Will log to PLogs
        log_plog_event.setOnClickListener {

            if (editText.text.isEmpty()) {
                PLog.logThis(TAG, "buttonOnClick", "Log: " + Math.random(), LogLevel.INFO)
            } else {
                PLog.logThis(TAG, "editTextData", editText.text.toString(), LogLevel.INFO)
            }
        }

        //Will Log to custom data logs, in Log File name & path provided in Builder
        log_data_log_event.setOnClickListener {

            var dataToLog = ""

            if (editText.text.isEmpty()) {
                dataToLog = "Log: " + Math.random() + "\n"

                locationsLog?.appendToFile(dataToLog)
                notificationsLog?.appendToFile(dataToLog)
                deliveriesLog?.appendToFile("Deliveries: $dataToLog")
            } else {
                dataToLog = editText.text.toString() + "\n"

                locationsLog?.appendToFile(dataToLog)
                notificationsLog?.appendToFile(dataToLog)
                deliveriesLog?.appendToFile("Deliveries: $dataToLog")
            }
        }

        //Will delete all Logs
        delete.setOnClickListener {

            //Will clear All PLogs
            PLog.clearLogs()

            Toast.makeText(this@MainActivity, "Logs Cleared!", Toast.LENGTH_SHORT).show()
        }

        //Will export PLogs
        export_plogs.setOnClickListener {

            PLog.exportLogsForType(ExportType.ALL, false)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            onNext = {
                                PLog.logThis(TAG, "exportPLogs", "PLogs Path: $it", LogLevel.INFO)
                                Toast.makeText(this@MainActivity, "Exported to: $it", Toast.LENGTH_SHORT).show()
                            },
                            onError = {
                                it.printStackTrace()
                                PLog.logThis(TAG, "exportPLogs", "PLog Error: " + it.message, LogLevel.ERROR)
                            },
                            onComplete = { }
                    )
        }


        //Will Export custom data log
        export_data_logs.setOnClickListener {

            PLog.exportAllDataLogs()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            onNext = {
                                PLog.logThis(TAG, "exportDataLogs", "DataLog Path: $it", LogLevel.INFO)
                                Toast.makeText(this@MainActivity, "Exported to: $it", Toast.LENGTH_SHORT).show()
                            },
                            onError = {
                                it.printStackTrace()
                                PLog.logThis(TAG, "exportDataLogs", "DataLogger Error: " + it.message, LogLevel.ERROR)
                            },
                            onComplete = { }
                    )
        }


        //Will print logged data in PLogs
        print_plogs.setOnClickListener {

            PLog.printLogsForType(ExportType.ALL)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            onNext = {
                                Log.i("PLog", it)
                            },
                            onError = {
                                it.printStackTrace()
                                PLog.logThis(TAG, "printLogs", "PLog Error: " + it.message, LogLevel.ERROR)
                            },
                            onComplete = { }
                    )
        }

        //Will print logged data in DataLogs
        print_data_logs.setOnClickListener {

            PLog.printDataLogsForName(LogType.Location.type)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                            onNext = {
                                Log.i("DataLog", it)
                            },
                            onError = {
                                it.printStackTrace()
                                PLog.logThis(TAG, "printLogs", "DataLogger Error: " + it.message, LogLevel.ERROR)
                            },
                            onComplete = { }
                    )
        }

        switch1.setOnCheckedChangeListener { compoundButton, b ->
            encryptLogs = b
        }

        print_error.setOnClickListener {
            val time = System.currentTimeMillis()

            if (time.toInt() % 2 == 0) {
                PLog.logThis(TAG, "reportError", info = "Some Info", exception = Exception("This is an Exception!"), level = LogLevel.ERROR)
            } else {
                PLog.logThis(TAG, "reportError", Throwable("This is an severe Throwable!"), LogLevel.SEVERE)
            }
        }
    }
}
