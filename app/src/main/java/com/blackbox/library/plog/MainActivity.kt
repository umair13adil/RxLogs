package com.blackbox.library.plog

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import com.blackbox.plog.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.io.File

class MainActivity : AppCompatActivity() {

    val TAG: String = "MainActivity"
    var PERMISSION_CODE = 9234;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //This must be initialized before calling PLog
        PLogBuilder()
                .setLogsSavePath(Environment.getExternalStorageDirectory().absolutePath + File.separator + "PLogTest")
                .setLogsExportPath(Environment.getExternalStorageDirectory().absolutePath + File.separator + "PLogTest" + File.separator + "ZippedLogs")
                .setExportFileName("MYFILENAME")
                .attachNoOfFilesToFiles(false)
                .attachTimeStampToFiles(false)
                .setLogFormatType(LogFormatter.FORMAT_CURLY)
                .debuggable(false)
                .logSilently(false)
                .setTimeStampFormat("DDMMYYYY")
                .build()

        //This must be initialized before calling DataLogger
        //Each DataLogger builder can be used to log different data files
        val myLogs: DataLogger = DataLogBuilder()
                .setLogsSavePath(Environment.getExternalStorageDirectory().absolutePath + File.separator + "DataLogTest")
                .setLogsExportPath(Environment.getExternalStorageDirectory().absolutePath + File.separator + "DataLogTest" + File.separator + "ZippedLogs")
                .setLogFileName("myLogs.txt")
                .setExportFileName("myLogsExported")
                .attachTimeStampToFiles(false)
                .debuggable(false)
                .build()


        val button_log = findViewById<Button>(R.id.button)
        val button_export = findViewById<Button>(R.id.export)
        val button_clear = findViewById<Button>(R.id.delete)


        //Check read write permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_CODE);
        }


        button_log.setOnClickListener {

            //Will log to PLogs
            PLog.logThis(TAG, "buttonOnClick", "Log: " + Math.random(), PLog.TYPE_INFO)

            //Will Log to custom data logs, in Log File name & path provided in Builder
            myLogs.appendToFile("Log: " + Math.random() + "\n");
        }

        button_clear.setOnClickListener {

            //Will clear All PLogs
            PLog.clearLogs()

            //Will clear All data logs for tha data location provided in builder
            myLogs.clearLogs()

            Toast.makeText(this@MainActivity, "Logs Cleared!", Toast.LENGTH_SHORT).show()
        }

        button_export.setOnClickListener {

            //Will export PLogs
            CompositeDisposable().add(PLog.getLogs(PLog.LOG_TODAY)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<String>() {

                        override fun onNext(filePath: String) {
                            PLog.logThis(TAG, "exportPLogs", "PLogs Path: " + filePath, PLog.TYPE_ERROR)
                            Toast.makeText(this@MainActivity, "Exported to: " + filePath, Toast.LENGTH_SHORT).show()
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                            PLog.logThis(TAG, "exportPLogs", "Error: " + e.message, PLog.TYPE_ERROR)
                        }

                        override fun onComplete() {

                        }
                    }))

            //Will Export custom data log
            CompositeDisposable().add(myLogs.getLogs()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<String>() {

                        override fun onNext(filePath: String) {
                            PLog.logThis(TAG, "exportDataLogs", "DataLog Path: " + filePath, PLog.TYPE_ERROR)
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                            PLog.logThis(TAG, "exportDataLogs", "Error: " + e.message, PLog.TYPE_ERROR)
                        }

                        override fun onComplete() {

                        }
                    }))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_CODE) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                PLog.logThis(TAG, "onRequestPermissionsResult", "Permissions Granted!", PLog.TYPE_INFO)
            } else {
                PLog.logThis(TAG, "onRequestPermissionsResult", "Permissions Not Granted!", PLog.TYPE_WARNING)
            }

        }
    }
}
