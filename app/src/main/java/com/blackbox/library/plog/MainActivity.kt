package com.blackbox.library.plog

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import com.blackbox.plog.elk.PLogMetaInfoProvider
import com.blackbox.plog.elk.models.fields.MetaInfo
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.exporter.ExportType
import com.blackbox.plog.pLogs.models.LogLevel
import com.blackbox.plog.pLogs.models.LogType
import com.mooveit.library.Fakeit
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val TAG: String = MainActivity::class.java.simpleName
    private var PERMISSION_CODE = 9234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Set MetaInfo for ELK Logs
        PLogMetaInfoProvider.elkStackSupported = true
        PLogMetaInfoProvider.setMetaInfo(MetaInfo(
                appId = BuildConfig.APPLICATION_ID,
                appName = getString(R.string.app_name),
                appVersion = BuildConfig.VERSION_NAME,
                language = "en-US",

                /**Environment**/
                deviceId = "12",
                environmentId = BuildConfig.FLAVOR,
                environmentName = BuildConfig.BUILD_TYPE,
                organizationId = "9778",

                /**User**/
                userId = "12112",
                userName = "Umair",
                userEmail = "m.umair.adil@gmail.com",

                /**Device**/
                deviceSerial = "SK-78",
                deviceBrand = Build.BRAND,
                deviceName = Build.DEVICE,
                deviceManufacturer = Build.MANUFACTURER,
                deviceModel = Build.MODEL,
                deviceSdkInt = Build.VERSION.SDK_INT.toString(),

                /**Location**/
                latitude = 0.0,
                longitude = 0.0,

                /**Labels**/
                labels = hashMapOf(Pair("env", "dev"))
        ))

        //Initialize FakeIt
        Fakeit.initWithLocale(Locale.ENGLISH)

        //Check read write permissions
        checkPermissions()

        //If permission granted
        setupLoggerControls()

        //Write Fake Data to Logs
        for (i in 0..100) {
            PLog.logThis(TAG, Fakeit.gameOfThrones().house(), Fakeit.gameOfThrones().quote(), LogLevel.INFO)
        }

        run_test.setOnClickListener {
            startActivity(Intent(this, HourlyLogsTest::class.java))
        }
    }

    private fun setupLoggerControls() {

        //Log edit Text events as soon as they are entered
        listenForInputText()

        //This will get 'DataLogger' object for predefined type in ConfigFile.
        val locationsLog = PLog.getLoggerFor(LogType.Location.type)
        val notificationsLog = PLog.getLoggerFor(LogType.Notification.type)
        val booksLogs = PLog.getLoggerFor("Books")

        //Will log to PLogs
        log_plog_event.setOnClickListener {

            //This will take care of putting logged data to current time & date's file
            PLog.logThis(TAG, "buttonOnClick", "Quote: " + Fakeit.harryPotter().quote(), LogLevel.INFO)
        }

        //Will Log to custom data logs, in Log File name & path provided in Builder
        log_data_log_event.setOnClickListener {

            val dataToLog = "Book: " + Fakeit.book().title() + "\n"
            booksLogs?.appendToFile("Book: $dataToLog")


            val locationToLog = "Location: " + Fakeit.address().streetAddress() + "\n"
            locationsLog?.appendToFile(locationToLog)


            val notificationToLog = "Food: " + Fakeit.food().spice() + "\n"
            notificationsLog?.appendToFile(notificationToLog)
        }

        //Will delete all Logs
        delete.setOnClickListener {
            clearLogs()
        }

        //Will export PLogs
        export_plogs.setOnClickListener {
            exportPLogs()
        }

        //Will Export custom data log
        export_data_logs.setOnClickListener {
            exportDataLogs()
        }


        //Will print logged data in PLogs
        print_plogs.setOnClickListener {
            printPLogs()
        }

        //Will print logged data in DataLogs
        print_data_logs.setOnClickListener {
            printDataLogs()
        }

        print_error.setOnClickListener {
            printException()
        }
    }

    private fun clearLogs() {

        //Will clear All PLogs
        PLog.clearLogs()

        Toast.makeText(this@MainActivity, "Logs Cleared!", Toast.LENGTH_SHORT).show()
    }

    private fun exportPLogs() {
        PLog.exportLogsForType(ExportType.TODAY, exportDecrypted = true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeBy(
                        onNext = {
                            PLog.logThis(TAG, "exportPLogs", "PLogs Path: $it", LogLevel.INFO)

                            runOnUiThread {
                                Toast.makeText(this@MainActivity, "Exported to: $it", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onError = {
                            it.printStackTrace()
                            PLog.logThis(TAG, "exportPLogs", "PLog Error: " + it.message, LogLevel.ERROR)
                        },
                        onComplete = { }
                )
    }

    private fun exportDataLogs() {
        PLog.exportAllDataLogs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeBy(
                        onNext = {
                            PLog.logThis(TAG, "exportDataLogs", "DataLog Path: $it", LogLevel.INFO)

                            runOnUiThread {
                                Toast.makeText(this@MainActivity, "Exported to: $it", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onError = {
                            it.printStackTrace()
                            PLog.logThis(TAG, "exportDataLogs", "DataLogger Error: " + it.message, LogLevel.ERROR)
                        },
                        onComplete = { }
                )
    }

    private fun printPLogs() {
        PLog.printLogsForType(ExportType.TODAY, printDecrypted = true)
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

    private fun printDataLogs() {
        PLog.printDataLogsForName(LogType.Location.type, printDecrypted = true)
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

    private fun printException() {
        val time = System.currentTimeMillis()

        if (time.toInt() % 2 == 0) {
            PLog.logThis(TAG, "reportError", info = "Some Info", exception = Exception("This is an Exception!"), level = LogLevel.ERROR)
        } else {
            PLog.logThis(TAG, "reportError", Throwable("This is an severe Throwable!"), LogLevel.SEVERE)
        }
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

    private fun checkPermissions() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_CODE)
            return
        }
    }

    private fun listenForInputText() {
        editText?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {
                PLog.logThis(TAG, "afterTextChanged", p0.toString(), LogLevel.INFO)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                PLog.logThis(TAG, "onTextChanged", p0.toString(), LogLevel.INFO)
            }
        })
    }
}
