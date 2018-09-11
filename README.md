# RxLogs Advanced Logging
[![Android Arsenal]( https://img.shields.io/badge/Android%20Arsenal-RxLogs-green.svg?style=flat )]( https://android-arsenal.com/details/1/6633 )
### PLogger and Data Logger
##### `A kotlin based advanced logging framework`. 

All logs are saved to files in storage path provided. These logs are helpful when developer wants to analyze user activities within the app. A new log file is created every hour on a user event. These logs can be filtered and sorted easily. Logs can easily be exported as zip file base on filter type. This zip file can be uploaded to server on export. PLogs also provide functionality to log arrange data logs into a predefined directory structure. These logs can be used for a specific event within the app.

![Alt text](pictures/feature.png?raw=true "Icon")
![Image1](pictures/picture1.png)

### Features:

1. Logs events in files created separately every hour with **'PLogs'** logger. (24 hours)
2. Files can be compressed and exported for time and day filters
3. Clear Logs easily
4. Save logs to custom path
5. Export Logs to custom path as zip file
6. RxJava2 support
7. Custom Log formatting
8. CSV support
9. Custom timestamps support
10. Custom data logging support with **'DataLogs'** logger.
11. Encryption support added
12. Auto Log system crashes
13. Multiple directory structures
14. Print logs as String
15. Export all or single types of logs
16. XML configuration support
17. Logging events
18. Advanced Automation

### Add to project:

Add module to your project:

    dependencies {
    
        //Rxjava Dependencies
        implementation 'io.reactivex.rxjava2:rxandroid:2.0.2'
        implementation  'io.reactivex.rxjava2:rxkotlin:2.2.0'
        
        //PLogger
        implementation project(':plog')
    }
    
# Usage:

## Setup PLogs:

Add following implementation in your Application class.

        class MainApplication : Application() {
        
            override fun onCreate() {
                super.onCreate()
        
                setUpPLogger() //Initialize PLogger here
            }
        }
        
         private fun setUpPLogger() {
                 val logsPath = Environment.getExternalStorageDirectory().absolutePath + File.separator + "PLogs"
                 
                         val logsConfig = LogsConfig(
                                 isDebuggable = true,
                                 savePath = logsPath,
                                 zipFileName = "MyLogs",
                                 exportPath = logsPath + File.separator + "PLogsOutput"
                         ).also {
                             it.getLogEventsListener()
                                     .doOnNext {
                 
                                         when (it.event) {
                                             EventTypes.NEW_ERROR_REPORTED -> {
                                                 PLog.logThis("PLogger", "event", it.data, LogLevel.INFO)
                                             }
                                             EventTypes.PLOGS_EXPORTED -> {
                                             }
                                             EventTypes.DATA_LOGS_EXPORTED -> {
                                             }
                                             EventTypes.LOGS_CONFIG_FOUND -> {
                                                 PLog.getLogsConfigFromXML()?.also {
                                                     PLog.logThis("PLogger", "event", Gson().toJson(it).toString(), LogLevel.INFO)
                                                 }
                                             }
                                             EventTypes.NEW_EVENT_DIRECTORY_CREATED -> {
                                                 PLog.logThis("PLogger", "event", "New directory created: " + it.data, LogLevel.INFO)
                                             }
                                         }
                                     }
                                     .subscribe()
                         }
                 
                  
                         PLog.setLogsConfig(logsConfig, saveToFile = true) //Initialize configurations
                       
             }

###### Log Types:

There are some predefined log types that can be used along with custom type. For each type defined in builder, logger object will be created and provided for later use.

    1. LogType.Device //To Log device related data
    2. LogType.Location //To Log Locations
    3. LogType.Notification //To Log notifications
    4. LogType.Network //To Log network calls
    5. LogType.Navigation //To Log user screen navigation
    6. LogType.History //To Log device & app history 
    7. LogType.Tasks //To Log Tasks performed
    8. LogType.Jobs //To Log Jobs data
    
Add these types to your Log configuration like this:

    val logsConfig = LogsConfig(
                            logTypesEnabled = arrayListOf(LogType.Notification.type, LogType.Location.type, LogType.Navigation.type, "Deliveries")
                    )
    
To access logger of these types simply call:

    val locationsLog = PLog.getLoggerFor(LogType.Location.type)
    locationsLog?.appendToFile("My Log Data!")
    
    val deliveriesLog = PLog.getLoggerFor("Deliveries")
    deliveriesLog?.overwriteToFile("My Log Data!")
                
### Log data to File:
    PLog.logThis(TAG, "buttonOnClick", "Log: " + Math.random(), LogLevel.INFO)
    
### Log Exceptions to File:
Both Exceptions & Throwable can be passed to logger.

    PLog.logExc(TAG, "uncaughtException", e)
    
Exceptions & Throwable can be tagged as severe by adding LogLevel.SEVERE:

    PLog.logExc(TAG, "uncaughtException", e, LogLevel.SEVERE)


###### Apply Encryption to Logs:
To enable AES encryption, set following fields in 'LogsConfig' builder:

        val logsConfig = LogsConfig(
                        encryptionEnabled = false,
                        encryptionKey = ""
                )
        
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
   
    1.  ExportType.TODAY
    2.  ExportType.LAST_HOUR
    3.  ExportType.WEEKS
    4.  ExportType.LAST_24_HOURS
    5.  ExportType.ALL
    
###### Log Severity Levels:

You can use following Log severity levels to tag logs:

    1. LogLevel.INFO
    2. LogLevel.WARNING
    3. LogLevel.ERROR
    4. LogLevel.SEVERE
    
### Clear Logs:
    PLog.clearLogs()
              
### Export PLogs:    

    PLog.exportLogsForType(ExportType.TODAY)
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
                        
### Print PLogs: 

        PLog.printLogsForType(ExportType.TODAY)
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
         
                
### Export Data Logs:

               PLog.exportAllDataLogs() 
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
                        
                                                
### Print Data Logs: 

            PLog.printDataLogsForName(LogType.Location.type)
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
