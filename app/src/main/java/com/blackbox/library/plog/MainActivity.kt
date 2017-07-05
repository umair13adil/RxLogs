package com.blackbox.library.plog

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.blackbox.plog.PLog
import io.vrinda.kotlinpermissions.PermissionCallBack
import io.vrinda.kotlinpermissions.PermissionsActivity

class MainActivity : PermissionsActivity() {

    val TAG: String = "MainActivity"
    val pLog = PLog.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var button_log = findViewById(R.id.button) as Button
        var button_export = findViewById(R.id.export) as Button

        requestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, object : PermissionCallBack {
            override fun permissionGranted() {
                super.permissionGranted()
                pLog.logThis(TAG, "requestPermissions", "Permission Granted!", pLog.TYPE_INFO)
            }

            override fun permissionDenied() {
                super.permissionDenied()
                Log.v("Permissions", "Denied")
            }
        })

        requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, object : PermissionCallBack {
            override fun permissionGranted() {
                super.permissionGranted()
                pLog.logThis(TAG, "requestPermissions", "Permission Granted!", pLog.TYPE_INFO)
            }

            override fun permissionDenied() {
                super.permissionDenied()
                Log.v("Permissions", "Denied")
            }
        })

        button_log.setOnClickListener {
            pLog.logThis(TAG, "buttonOnClick", "Log: " + Math.random(), pLog.TYPE_INFO)
        }

        button_export.setOnClickListener {
            pLog.getLogs(pLog.LOG_TODAY)
        }
    }
}
