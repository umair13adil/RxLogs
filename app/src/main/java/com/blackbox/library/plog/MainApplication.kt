package com.blackbox.library.plog

import android.app.Application
import com.blackbox.plog.utils.AppExceptionHandler

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        setupCrashHandler()
    }

    private fun setupCrashHandler() {
        val systemHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { t, e -> /* do nothing */ }

        val fabricExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler(AppExceptionHandler(systemHandler, fabricExceptionHandler, this))
    }

}


