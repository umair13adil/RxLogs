package com.blackbox.plog.tests

import com.blackbox.plog.utils.DateTimeUtils

object PLogTestHelper {

    var isTestingHourlyLogs = false
    var hourlyLogFileName = DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis())
}