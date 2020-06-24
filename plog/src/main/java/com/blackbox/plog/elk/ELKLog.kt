package com.blackbox.plog.elk

data class ELKLog(var tag: String?, var subTag: String?, var logMessage: String?, var timeStamp: String?, var severity: String?)