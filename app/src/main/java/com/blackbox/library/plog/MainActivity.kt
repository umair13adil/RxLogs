package com.blackbox.library.plog

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.blackbox.plog.dataLogs.DataLogBuilder
import com.blackbox.plog.dataLogs.DataLogger
import com.blackbox.plog.pLogs.LogFormatter
import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.PLogBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    val TAG: String = MainActivity::class.java.simpleName
    var PERMISSION_CODE = 9234;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //This must be initialized before calling PLog
        PLogBuilder()
                .setLogsSavePath(Environment.getExternalStorageDirectory().absolutePath + File.separator + "PLogTest")
                .setLogsExportPath(Environment.getExternalStorageDirectory().absolutePath + File.separator + "PLogTest" + File.separator + "ZippedLogs")
                .debuggable(true)
                .setLogFileExtension(".txt")
                .setLogFormatType(LogFormatter.FORMAT_CURLY)
                .attachTimeStampToFiles(true)
                .setTimeStampFormat("dd MMMM yyyy kk:mm:ss")
                .build()

        //This must be initialized before calling DataLogger
        //Each DataLogger builder can be used to log different data files
        val myLogs: DataLogger = DataLogBuilder()
                .setLogsSavePath(Environment.getExternalStorageDirectory().absolutePath + File.separator + "DataLogTest")
                .setLogsExportPath(Environment.getExternalStorageDirectory().absolutePath + File.separator + "DataLogTest" + File.separator + "ZippedLogs")
                .setLogFileName("myLogs.txt")
                .setExportFileName("myLogsExported")
                .attachTimeStampToFiles(false)
                .debuggable(true)
                .build()


        //Check read write permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_CODE);
        }


        button.setOnClickListener {

            //Will log to PLogs
            PLog.logThis(TAG, "buttonOnClick", "Log: " + Math.random(), PLog.TYPE_INFO)

            //Will Log to custom data logs, in Log File name & path provided in Builder
            myLogs.appendToFile("Log: " + Math.random() + "\n");
        }

        delete.setOnClickListener {

            //Will clear All PLogs
            PLog.clearLogs()

            //Will clear All data logs for tha data location provided in builder
            myLogs.clearLogs()

            Toast.makeText(this@MainActivity, "Logs Cleared!", Toast.LENGTH_SHORT).show()
        }

        export.setOnClickListener {

            //Will export PLogs
            PLog.getLogs(PLog.LOG_TODAY)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(  // named arguments for lambda Subscribers
                            onNext = {
                                PLog.logThis(TAG, "exportPLogs", "PLogs Path: $it", PLog.TYPE_INFO)
                                Toast.makeText(this@MainActivity, "Exported to: $it", Toast.LENGTH_SHORT).show()
                            },
                            onError = {
                                it.printStackTrace()
                                PLog.logThis(TAG, "exportPLogs", "Error: " + it.message, PLog.TYPE_ERROR)
                            },
                            onComplete = { }
                    )

            //Will Export custom data log
            myLogs.logs
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(  // named arguments for lambda Subscribers
                            onNext = {
                                PLog.logThis(TAG, "exportDataLogs", "DataLog Path: $it", PLog.TYPE_INFO)
                                Toast.makeText(this@MainActivity, "Exported to: $it", Toast.LENGTH_SHORT).show()
                            },
                            onError = {
                                it.printStackTrace()
                                PLog.logThis(TAG, "exportDataLogs", "Error: " + it.message, PLog.TYPE_ERROR)
                            },
                            onComplete = { }
                    )
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
