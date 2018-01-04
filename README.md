# RxLogs
### PLogs & DataLogs
##### An android/kotlin RxJava based advanced logging framework. 

All logs are saved to files in storage path provided. These logs are helpful when developer wants to analyze user activities within the app. A new log file is created every hour on a user event. These logs can be filtered and sorted easily. Logs can easily be exported as zip file base on filter type. This zip file can be uploaded to server easily. PLogs also provide functionality to log separate data logs. These logs can be for a specific event with the app. For example they can be used to log location events of users, so that file will only contain location logs.

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

You can apply following log formats:

    1. LogFormatter.FORMAT_CURLY.
    2. LogFormatter.FORMAT_SQUARE.
    3. LogFormatter.FORMAT_CSV.
    4. LogFormatter.FORMAT_CUSTOM.

To apply CSV format, you need to provide deliminator, by default it is comma ','.
To apply custom formats you need to provide opening & closing character. Like {},[], ' ' etc

    1. {TAG}   {FUNCTION_NAME}   {YOUR_LOGGED_EVENT}   {07:05:2017 11:22:17 AM}   {Info}
    2. [TAG]   [FUNCTION_NAME]   [YOUR_LOGGED_EVENT]   [07:05:2017 11:22:17 AM]   [Info]

###### CSV Support:
    TAG;FUNCTION_NAME;YOUR_LOGGED_EVENT;07:05:2017 11:22:17 AM;Info

###### File Name Format:
    DDMMYYYHH-> 0207201700

File Name consists of: {Day} {Month} {Year} {Hour} Hours are in 24h format.

###### Time Stamp Format:

Each log entry has timestamp associated with it. You can modify it's format. 
By default it will be like this:

    "dd MMMM yyyy hh:mm:ss a"

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
                
                
## MIT License

##### Copyright (c) 2018 Muhammad Umair Adil

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
