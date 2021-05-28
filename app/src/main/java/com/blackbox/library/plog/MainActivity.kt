package com.blackbox.library.plog

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.blackbox.plog.elk.PLogMetaInfoProvider
import com.blackbox.plog.elk.models.fields.MetaInfo
import com.blackbox.plog.mqtt.PLogMQTTProvider
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.exporter.ExportType
import com.blackbox.plog.pLogs.models.LogLevel
import com.blackbox.plog.pLogs.models.LogType
import com.mooveit.library.BuildConfig
import com.mooveit.library.Fakeit
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val _tag: String = MainActivity::class.java.simpleName
    private var code = 9234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        doOnPermissionsSet()

        //Check read write permissions
        checkPermissions()
    }

    private fun doOnPermissionsSet() {
        MainApplication.setUpPLogger(this)

        //Set MetaInfo for ELK Logs
        PLogMetaInfoProvider.elkStackSupported = false
        PLogMetaInfoProvider.setMetaInfo(
            MetaInfo(
                /**App**/
                appId = BuildConfig.APPLICATION_ID,
                appName = getString(R.string.app_name),
                appVersion = BuildConfig.VERSION_NAME,
                language = "en-US",

                /**Environment**/
                environmentId = BuildConfig.APPLICATION_ID,
                environmentName = BuildConfig.BUILD_TYPE,

                /**Organization**/
                organizationId = "9975",
                organizationUnitId = "24",
                organizationName = "BlackBox",

                /**User**/
                userId = "12112",
                userName = "Umair",
                userEmail = "m.umair.adil@gmail.com",
                deviceId = "12",

                /**Device**/
                deviceSerial = "SK-78",
                deviceBrand = Build.BRAND,
                deviceName = Build.DEVICE,
                deviceManufacturer = Build.MANUFACTURER,
                deviceModel = Build.MODEL,
                deviceSdkInt = Build.VERSION.SDK_INT.toString(),
                batteryPercent = "87",

                /**Location**/
                latitude = 0.0,
                longitude = 0.0,

                /**Labels**/
                labels = hashMapOf(Pair("env", "dev"))
            )
        )

        //MQTT Setup
        //Uncomment following block to enable MQTT feature
        /*PLogMQTTProvider.initMQTTClient(this,
                topic = "",
                brokerUrl = "", //Without Scheme
                certificateRes = R.raw.m2mqtt_ca,
                port = "8883",
                writeLogsToLocalStorage = true,
                initialDelaySecondsForPublishing = 30,
                debug = true
        )*/

        //Initialize FakeIt
        Fakeit.initWithLocale(Locale.ENGLISH)

        //If permission granted
        setupLoggerControls()

        //Write Fake Data to Logs
        /*for (i in 0..250) {
            PLog.logThis(
                _tag,
                Fakeit.gameOfThrones().house(),
                Fakeit.gameOfThrones().quote(),
                LogLevel.INFO
            )
        }*/

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
            PLog.logThis(
                _tag,
                "buttonOnClick",
                "Quote: " + Fakeit.harryPotter().quote(),
                LogLevel.INFO
            )
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
        print_plogs_hour.setOnClickListener {
            printPLogs(ExportType.LAST_HOUR)
        }

        print_plogs_day.setOnClickListener {
            printPLogs(ExportType.TODAY)
        }

        print_all.setOnClickListener {
            printPLogs(ExportType.ALL)
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
        PLog.exportLogsForType(
            ExportType.TODAY,
            exportDecrypted = MainApplication.isEncryptionEnabled
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    Log.i(_tag, "exportPLogs: PLogs Path: $it")

                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Exported to: $it", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                onError = {
                    it.printStackTrace()
                    Log.i(_tag, "exportPLogs: PLog Error: " + it.message)
                },
                onComplete = { }
            )
    }

    private fun exportDataLogs() {
        PLog.exportAllDataLogs(exportDecrypted = MainApplication.isEncryptionEnabled)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    Log.i(_tag, "exportDataLogs: DataLog Path: $it")

                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Exported to: $it", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                onError = {
                    it.printStackTrace()
                    Log.i(_tag, "exportDataLogs: DataLogger Error: " + it.message)
                },
                onComplete = { }
            )
    }

    private fun printPLogs(exportType: ExportType) {
        PLog.printLogsForType(exportType, printDecrypted = MainApplication.isEncryptionEnabled)
            .retry(2)
            .subscribeBy(
                onNext = {
                    Log.i("PLog", it)
                },
                onError = {
                    it.printStackTrace()
                    Log.i(_tag, "printLogs: PLog Error: " + it.message)
                },
                onComplete = {
                    PLog.logThis(_tag, "print_plogs", "Print PLogs Completed.")
                }
            )
    }

    private fun printDataLogs() {
        PLog.printDataLogsForName(
            LogType.Location.type,
            printDecrypted = MainApplication.isEncryptionEnabled
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    Log.i("DataLog", it)
                },
                onError = {
                    it.printStackTrace()
                    Log.i(_tag, "printLogs: DataLogger Error: " + it.message)
                },
                onComplete = { }
            )
    }

    private fun printException() {
        val time = System.currentTimeMillis()

        if (time.toInt() % 2 == 0) {
            PLog.logThis(
                _tag,
                "reportError",
                info = "Some Info",
                exception = Exception("This is an Exception!"),
                level = LogLevel.ERROR
            )
        } else {
            PLog.logThis(
                _tag,
                "reportError",
                Throwable("This is an severe Throwable!"),
                LogLevel.SEVERE
            )
        }

        try {
            val a = arrayListOf<Int>()
            val d = a[2]
            Log.i(_tag, d.toString())
        } catch (exception: java.lang.Exception) {
            PLog.logThis(_tag, "printException", exception = exception, level = LogLevel.ERROR)
        }

        try {
            val a = 0 / 1
            val d = a - 50
            Log.i(_tag, d.toString())
        } catch (throwable: Throwable) {
            PLog.logThis(_tag, "printException", throwable = throwable, level = LogLevel.ERROR)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == code) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                Log.i(_tag, "onRequestPermissionsResult: Permissions Granted!")

                doOnPermissionsSet()

            } else {
                Log.i(_tag, "onRequestPermissionsResult: Permissions Not Granted!")
            }

        }
    }

    private fun checkPermissions() {

        if (!arePermissionsGranted()) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                code
            )
            return
        }
    }

    private fun arePermissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun listenForInputText() {
        editText?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {
                PLog.logThis(_tag, "afterTextChanged", p0.toString(), LogLevel.INFO)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                PLog.logThis(_tag, "onTextChanged", p0.toString(), LogLevel.INFO)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()

        PLogMQTTProvider.disposeMQTTClient()
    }
}
