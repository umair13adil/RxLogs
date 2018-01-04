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
    ''' //This must be initialized before calling PLog
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
                ''''
