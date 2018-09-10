# RxLogs
[![Android Arsenal]( https://img.shields.io/badge/Android%20Arsenal-RxLogs-green.svg?style=flat )]( https://android-arsenal.com/details/1/6633 )
### PLogs and DataLogs
##### `An android/kotlin RxJava based advanced logging framework`. 

All logs are saved to files in storage path provided. These logs are helpful when developer wants to analyze user activities within the app. A new log file is created every hour on a user event. These logs can be filtered and sorted easily. Logs can easily be exported as zip file base on filter type. This zip file can be uploaded to server easily. PLogs also provide functionality to log separate data logs. These logs can be for a specific event with the app. For example they can be used to log location events of users, so that file will only contain location logs.

![Alt text](pictures/feature.png?raw=true "Icon")
![Image1](pictures/picture1.png)

### Features:

1. Logs events in files created separately every hour with **'PLog'** logger. (24 hours)
2. Files can be compressed and exported for time and day filters
3. Clear Logs easily
4. Save logs to custom path
5. Export Logs to custom path as zip file
6. RxJava2 support
7. Custom Log formats
8. CSV support
9. Custom timestamps support
10. Custom data logging support with **'DataLogs'** logger.
11. Encryption support added
12. Auto Log system crashes

### Usage:

Add module to your project:

    dependencies {
    
        //Rxjava Dependencies
        implementation 'io.reactivex.rxjava2:rxandroid:2.0.2'
        implementation  'io.reactivex.rxjava2:rxkotlin:2.2.0'
        
        //PLogger
        implementation project(':plog')
    }

###### Apply Encryption to Logs:
To enable AES encryption, set following to builder:

        .enableEncryption(true) //Enable Encryption
        .setEncryptionKey("YOUR_ENCRYPTION_KEY") //Set Encryption Key
        
Key length should be greater than 32.

###### Apply Custom Formats:

You can apply following log formats:

    1. LogFormatter.FORMAT_CURLY
    2. LogFormatter.FORMAT_SQUARE
    3. LogFormatter.FORMAT_CSV
    4. LogFormatter.FORMAT_CUSTOM

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
There are multiple formats available to choose from

        1.  TimeStampFormat.DATE_FORMAT_1 // "ddMMyyyy"
        2.  TimeStampFormat.DATE_FORMAT_2 // "MM/dd/yyyy"
        3.  TimeStampFormat.TIME_FORMAT_FULL_JOINED // "ddMMyyyy_kkmmss_a"
        4.  TimeStampFormat.TIME_FORMAT_FULL_1 // "dd MMMM yyyy kk:mm:ss"
        5.  TimeStampFormat.TIME_FORMAT_FULL_2 // "MM:dd:yyyy hh:mm:ss a"
        6.  TimeStampFormat.TIME_FORMAT_24_FULL // "dd/MM/yyyy kk:mm:ss"
        7.  TimeStampFormat.TIME_FORMAT_READABLE // "dd MMMM yyyy hh:mm:ss a"
        8.  TimeStampFormat.TIME_FORMAT_SIMPLE // "kk:mm:ss"

###### Export Filters:
   
   Logs can be exported with following request filters:
   
    1.  LogRequestType.TODAY
    2.  LogRequestType.LAST_HOUR,
    3.  LogRequestType.WEEKS,
    4.  LogRequestType.LAST_24_HOURS
    
###### Log Types:

You can use following Log Types to identify type:

    1. LogLevel.INFO
    2. LogLevel.WARNING
    3. LogLevel.ERROR
    4. LogLevel.SEVERE

# Usage:

## Setup PLogs:

        class MainApplication : Application() {
        
            override fun onCreate() {
                super.onCreate()
        
                setUpPLogger() //Initialize PLogger here
            }
        }
        
         private fun setUpPLogger() {
                 val logsPath = Environment.getExternalStorageDirectory().absolutePath + File.separator + "PLogs"
         
                 val logsConfig = LogsConfig(
                         logLevelsEnabled = arrayListOf(LogLevel.ERROR, LogLevel.SEVERE, LogLevel.INFO, LogLevel.WARNING),
                         logTypesEnabled = arrayListOf("Locations", "APIs", "Notifications"),
                         formatType = FormatType.FORMAT_CURLY,
                         logsRetentionPeriodInDays = 7,
                         zipsRetentionPeriodInDays = 7,
                         autoClearLogsOnExport = true,
                         enabled = true,
                         exportFileNamePreFix = "",
                         exportFileNamePostFix = "",
                         autoExportErrors = true,
                         encryptionEnabled = false,
                         encryptionKey = "",
                         singleLogFileSize = 2048 * 2,
                         logFilesLimit = 30,
                         directoryStructure = DirectoryStructure.FOR_DATE,
                         logSystemCrashes = true,
                         autoExportLogTypes = arrayListOf("Notifications", "Locations"),
                         autoExportLogTypesPeriod = 3,
                         logsDeleteDate = "",
                         zipDeleteDate = "",
                         savePath = logsPath,
                         exportPath = logsPath + File.separator + "PLogsOutput",
                         zipFileName = "MyLogs",
                         isDebuggable = true,
                         logFileExtension = LogExtension.TXT,
                         attachTimeStamp = false,
                         attachNoOfFiles = true,
                         timeStampFormat = TimeStampFormat.DATE_FORMAT_1,
                         nameForEventDirectory = "My Name",
                         zipFilesOnly = false
                 ).also {
                     it.getLogEventsListener()
                             .doOnNext {
                                 PLog.logThis("PLogger", "getLogEventsListener", "Event: $it", LogLevel.INFO)
         
                                 PLog.getLogsConfigFromXML()?.also {
                                     PLog.logThis("PLog", "XML", Gson().toJson(it).toString(), LogLevel.INFO)
                                 }
                             }
                             .subscribe()
                 }
         
                 logsConfig.setEventNameForDirectory("My Name 2")
                 PLog.setLogsConfig(logsConfig, saveToFile = true)
             }
                
### Log data to File:
    PLog.logThis(TAG, "buttonOnClick", "Log: " + Math.random(), LogLevel.INFO)
    
### Log Exceptions to File:
Both Exceptions & Throwable can be passed to logger.

    PLog.logExc(TAG, "uncaughtException", e)
    
Exceptions & Throwable can be tagged as severe by adding LogLevel.SEVERE:

    PLog.logExc(TAG, "uncaughtException", e, LogLevel.SEVERE)

### Clear Logs:
    PLog.clearLogs()
              
### Export Logs:    

    PLog.getZippedLogs(LogRequestType.TODAY, true) //Set true, if logs exported should be decrypted
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(  
                                onNext = {
                                    PLog.logThis(TAG, "exportPLogs", "PLogs Path: $it", LogLevel.INFO)
                                    Toast.makeText(this@MainActivity, "Exported to: $it", Toast.LENGTH_SHORT).show()
                                },
                                onError = {
                                    it.printStackTrace()
                                    PLog.logThis(TAG, "exportPLogs", "Error: " + it.message, LogLevel.ERROR)
                                },
                                onComplete = { }
                        )
                        
### Print Logs: 

        PLog.getLoggedData(LogRequestType.TODAY)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeBy(
                                    onNext = {
                                        Log.i("PLog", "$it")
                                    },
                                    onError = {
                                        it.printStackTrace()
                                        PLog.logThis(TAG, "printLogs", "PLog Error: " + it.message, LogLevel.ERROR)
                                    },
                                    onComplete = { }
                            )
         
## Setup DataLogger

            val logsPath = Environment.getExternalStorageDirectory().absolutePath + File.separator + "PLogs"
            
            val myLogs: DataLogger = DataLogBuilder()
                            .setLogsSavePath(logsPath) 
                            .setLogsExportPath(logsPath + File.separator + "ZippedLogs")
                            .setLogFileName("myLogs.txt")
                            .setExportFileName("myLogsExported")
                            .attachTimeStampToFiles(false)
                            .debuggable(true)
                            .enableEncryption(true) //Enable Encryption
                            .setEncryptionKey("YOUR_ENCRYPTION_KEY") //Set Encryption Key
                            .enabled(true)
                            .build()
                
### Export Logs:

                myLogs.getZippedLogs(true) //Set true, if logs exported should be decrypted
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(  
                                onNext = {
                                    PLog.logThis(TAG, "exportDataLogs", "DataLog Path: $it", LogLevel.INFO)
                                    Toast.makeText(this@MainActivity, "Exported to: $it", Toast.LENGTH_SHORT).show()
                                },
                                onError = {
                                    it.printStackTrace()
                                    PLog.logThis(TAG, "exportDataLogs", "Error: " + it.message, LogLevel.ERROR)
                                },
                                onComplete = { }
                        )
                        
                                                
### Print Logs: 

            myLogs.getLoggedData()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeBy(
                                        onNext = {
                                            Log.i("DataLog", "$it")
                                        },
                                        onError = {
                                            it.printStackTrace()
                                            PLog.logThis(TAG, "printLogs", "DataLogger Error: " + it.message, LogLevel.ERROR)
                                        },
                                        onComplete = { }
                                )

### Log to custom file:
    
    //This Will append data to log file
    myLogs.appendToFile("Log: " + Math.random() + "\n");
    
    //This Will overwrite data to log file
    myLogs.overwriteToFile("Log: " + Math.random() + "\n");
    
### Clear Logs:
    myLogs.clearLogs()
                
                
## Auto Log System Crashes

Add this to onCreate of Application class to auto catch system crashes. This also works with Crashlytics.
Source Medium Article: [Hide your crashes gracefully (and still report them)](https://proandroiddev.com/hide-your-crashes-gracefully-and-still-report-them-9b1c85b25875)

        class MainApplication : Application() {
        
            override fun onCreate() {
                super.onCreate()
        
                setupCrashHandler()
            }
        
            private fun setupCrashHandler() {
                val systemHandler = Thread.getDefaultUncaughtExceptionHandler()
                
                Thread.setDefaultUncaughtExceptionHandler { t, e -> /* do something here */ }
                
                Fabric.with(this, Crashlytics())
        
                val fabricExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
                
                Thread.setDefaultUncaughtExceptionHandler(AppExceptionHandler(systemHandler, fabricExceptionHandler, this))
            }
        
        }
                
## MIT License

##### Copyright (c) 2018 Muhammad Umair Adil

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
