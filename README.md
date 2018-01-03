# PLog
An android kotlin based logger.

# Features:

1. Logs events in files created seprately every hour.
2. Files can be compressed and exported for three categories:

    a. Last Hour
    
    b. Today
    
    c. Last Week

# Format:

{TAG}   {FUNCTION_NAME}   {YOUR_LOGGED_EVENT}   {07:05:2017 11:22:17 AM}   {Info}

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
