# Rx PLogs & DataLogs
An android/kotlin RxJava based advanced logging framework.

# Features:

1. Logs events in files created seprately every hour. (24 hours)
2. Files can be compressed and exported for these filters:

    a. Last Hour
    
    b. Today
    
    c. Last Week
    
    d. Last 2 Days
    
3. Clear Logs easily.
4. Save logs to custom path.
5. Export Logs to custom path as zip file.
6. RxJava2 support.
7. Custom Log formats.
8. CSV support.
9. Custom timestamps support.
10. Custom data logging support with 'DataLogs'.
  

# Apply Custom Formats:

{TAG}   {FUNCTION_NAME}   {YOUR_LOGGED_EVENT}   {07:05:2017 11:22:17 AM}   {Info}
[TAG]   [FUNCTION_NAME]   [YOUR_LOGGED_EVENT]   [07:05:2017 11:22:17 AM]   [Info]

# CSV Support:

TAG;FUNCTION_NAME;YOUR_LOGGED_EVENT;07:05:2017 11:22:17 AM;Info


# File Name Format:

DDMMYYYHH-> 0207201700

File Name consists of: {Day} {Month} {Year} {Hour}


# Usage:

    Write Logs:

    val pLog = PLog.create()
    
    button_log.setOnClickListener {
            pLog.logThis(TAG, "buttonOnClick", "Log: " + Math.random(), pLog.TYPE_INFO)
    }
    
    Get Logs:
    
        pLog.getLogs(pLog.LOG_TODAY) //For Today's
        pLog.getLogs(pLog.LOG_LAST_HOUR) //For Last Hour
        pLog.getLogs(pLog.LOG_WEEK) //For Week's
