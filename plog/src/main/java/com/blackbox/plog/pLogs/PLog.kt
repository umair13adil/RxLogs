package com.blackbox.plog.pLogs

/**
 * Created by Umair Adil on 12/04/2017.
 */

import android.util.Log
import com.blackbox.plog.pLogs.impl.PLogImpl
import com.blackbox.plog.utils.RxBus

object PLog : PLogImpl() {

    init {

        //Setup RxBus for notifications.
        setLogBus(RxBus())

        Log.i("PLog", "PLogger Initialized!")
    }
}
