# Rx PLogs & DataLogs
An android/kotlin RxJava based advanced logging framework.

### Features:
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
  

###### Apply Custom Formats:

1. {TAG}   {FUNCTION_NAME}   {YOUR_LOGGED_EVENT}   {07:05:2017 11:22:17 AM}   {Info}
2. [TAG]   [FUNCTION_NAME]   [YOUR_LOGGED_EVENT]   [07:05:2017 11:22:17 AM]   [Info]

###### CSV Support:
TAG;FUNCTION_NAME;YOUR_LOGGED_EVENT;07:05:2017 11:22:17 AM;Info

###### File Name Format:
DDMMYYYHH-> 0207201700

File Name consists of: {Day} {Month} {Year} {Hour}


# Usage:

## For PLogs:

    PLogBuilder()
                .setLogsSavePath(Environment.getExternalStorageDirectory().absolutePath + File.separator + "PLogTest")
                .setLogsExportPath(Environment.getExternalStorageDirectory().absolutePath + File.separator + "PLogTest" +   File.separator + "ZippedLogs")
                .setExportFileName("MYFILENAME")
                .attachNoOfFilesToFiles(false)
                .attachTimeStampToFiles(false)
                .setLogFormatType(LogFormatter.FORMAT_CURLY)
                .debuggable(false)
                .logSilently(false)
                .setTimeStampFormat("DDMMYYYY")
                .build()
                
### To Log to File:
    PLog.logThis(TAG, "buttonOnClick", "Log: " + Math.random(), PLog.TYPE_INFO)
    
### To Clear Logs:
    PLog.clearLogs()
              
### To Export Logs:    

    CompositeDisposable().add(PLog.getLogs(PLog.LOG_TODAY)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<String>() {

                        override fun onNext(filePath: String) {
                            PLog.logThis(TAG, "exportPLogs", "PLogs Path: " + filePath, PLog.TYPE_ERROR)
                            Toast.makeText(this@MainActivity, "Exported to: " + filePath, Toast.LENGTH_SHORT).show()
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                            PLog.logThis(TAG, "exportPLogs", "Error: " + e.message, PLog.TYPE_ERROR)
                        }

                        override fun onComplete() {

                        }
                    }))
                
## For Data Logs:

    val myLogs: DataLogger = DataLogBuilder()
                .setLogsSavePath(Environment.getExternalStorageDirectory().absolutePath + File.separator + "DataLogTest")
                .setLogsExportPath(Environment.getExternalStorageDirectory().absolutePath + File.separator + "DataLogTest" + File.separator + "ZippedLogs")
                .setLogFileName("myLogs.txt")
                .setExportFileName("myLogsExported")
                .attachTimeStampToFiles(false)
                .debuggable(false)
                .build()

### To Log to File:
    
    //This Will append data to log file
    myLogs.appendToFile("Log: " + Math.random() + "\n");
    
    //This Will overwrite data to log file
    myLogs.overwriteToFile("Log: " + Math.random() + "\n");
    
### To Clear Logs:
    myLogs.clearLogs()
    
### To Export Logs:  

    CompositeDisposable().add(myLogs.getLogs()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<String>() {

                        override fun onNext(filePath: String) {
                            PLog.logThis(TAG, "exportDataLogs", "DataLog Path: " + filePath, PLog.TYPE_ERROR)
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                            PLog.logThis(TAG, "exportDataLogs", "Error: " + e.message, PLog.TYPE_ERROR)
                        }

                        override fun onComplete() {

                        }
                    }))
                
