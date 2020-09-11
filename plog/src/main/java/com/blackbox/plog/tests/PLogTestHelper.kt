package com.blackbox.plog.tests

import androidx.annotation.Keep
import com.blackbox.plog.utils.DateTimeUtils

@Keep
object PLogTestHelper {

    var isTestingHourlyLogs = false
    var hourlyLogFileName = DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis())
}