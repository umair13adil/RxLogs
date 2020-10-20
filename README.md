# RxLogs Advanced Logging
[![Android Arsenal]( https://img.shields.io/badge/Android%20Arsenal-RxLogs-green.svg?style=flat )]( https://android-arsenal.com/details/1/6633 )
### PLog and DataLog (Loggers)
##### `A file based advanced logging framework written in Kotlin`.

[![](https://jitpack.io/v/umair13adil/RxLogs.svg)](https://jitpack.io/#umair13adil/RxLogs)

Overview
--------

PLogs provides quick & simple file logging solution. All logs are saved to files in storage path provided. These logs are helpful when developer wants to analyze user activities within the app. A new log file is created every hour on a user event. These logs can be filtered and sorted easily. Logs can easily be exported as zip file base on filter type. This zip file can be uploaded to server on export. PLogs also provide functionality to arrange data logs into a predefined directory structure. These logs can be used for a specific events within the app. Logs can be saved as encrypted data.

![Alt text](pictures/feature.png?raw=true "Icon")
![Image1](pictures/picture1.png)
![Image2](pictures/picture2.png)

##### Read more about RxLogs usage on this Medium article: 

[Sending logs from apps in real-time using ELK stack & MQTT](https://itnext.io/sending-logs-from-flutter-apps-in-real-time-using-elk-stack-mqtt-c24fa0cb9802)

Features
--------

1. Logs events in files created separately every hour with **'PLogs'** logger. (24 hours)
2. Files can be compressed and exported for time and day filters
3. Clear Logs easily
4. Save logs to app's storage path
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
16. XML configuration support for internal persistence
17. Logger events Subscription
18. Advanced Automation for deleting logs automatically
19. Exports HTML formatted exceptions
20. ELK Stack Supported See more about it [here](https://www.elastic.co/what-is/elk-stack).
21. MQTT Support
22. Added logs queueing for offline support (MQTT Feature)

Setup
-------------

Add module to your project:

## Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:
```groovy
    allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```
## Step 2. Add the dependency

```groovy
    dependencies {
    implementation 'com.github.umair13adil:RxLogs:[Latest_Version]'
    }
```
    
Usage
-------------

Add following implementation in your Application class.

```kotlin
class MainApplication : Application() {
    
        override fun onCreate() {
            super.onCreate()
    
            setUpPLogger() //Initialize PLogger here
        }

private fun setUpPLogger() {
             val logsPath = "PLogs"
             
                     val logsConfig = LogsConfig(
                             isDebuggable = true,
                             savePath = logsPath,
                             zipFileName = "MyLogs",
                             exportPath = logsPath + File.separator + "PLogsOutput"
                     )

                     PLog.applyConfigurations(logsConfig, saveToFile = true, context = context) //Initialize configurations
                   
         }
}
```

#### Where are my logs stored?
_______________________________________________
 
 Your logs can be found in the path of your app's directory in storage:
 
*--> Android/data/[YOUR_APP_PACKAGE]/files/[YOUR_LOGS_FOLDER_NAME]/Logs/*
 
 ![Image3](pictures/picture3.png)

#### To Log data to file simply call like this
_______________________________________________

**1. Simple Info Log**

```kotlin
    PLog.logThis(TAG, "method_name", "Log: " + Math.random(), LogLevel.INFO)
```

**2. Simple Warning Log**

```kotlin
    PLog.logThis(TAG, "method_name", "This is a warning message!", LogLevel.WARNING)
```

**3. Error Log**

```kotlin
    PLog.logThis(TAG, "method_name", "This is a error message!", LogLevel.ERROR)
```

**4. Severe Log**

```kotlin
    PLog.logThis(TAG, "method_name", "This is a severe error message!", LogLevel.SEVERE)
```

**5. Exception Log**

```kotlin
    PLog.logThis(TAG, "reportError", Exception("This is an Exception!"))
```

**6. Throwable Log**

```kotlin
    PLog.logThis(TAG, "reportError", Throwable("This is an Throwable!"))
```

**7. Exception Log with Info**

```kotlin
    PLog.logThis(TAG, "reportError", info = "Some Info", exception = Exception("This is an Exception!"), level =  LogLevel.ERROR)
```

**8. Throwable Log with Info**

```kotlin
    PLog.logThis(TAG, "reportError", info = "Some Info", throwable = Throwable("This is an Exception!"), level =  LogLevel.SEVERE)
```

#### ELK Elastic Stack Support
_______________________________________________

To configure set this flag to true:

```kotlin
    PLogMetaInfoProvider.elkStackSupported = true
```

Send additional Meta info for better filtering at LogStash dashboard. This has to be set once on application start.

```kotlin

    PLogMetaInfoProvider.setMetaInfo(MetaInfo(
                /**App**/
                appId = BuildConfig.APPLICATION_ID,
                appName = getString(R.string.app_name),
                appVersion = BuildConfig.VERSION_NAME,
                language = "en-US",

                /**Environment**/
                environmentId = BuildConfig.APPLICATION_ID,
                environmentName = BuildConfig.BUILD_TYPE,

                /**Organization**/
                organizationId = "9975",
                organizationUnitId = "24",
                organizationName = "BlackBox",

                /**User**/
                userId = "12112",
                userName = "Umair",
                userEmail = "m.umair.adil@gmail.com",
                deviceId = "12",

                /**Device**/
                deviceSerial = "SK-78",
                deviceBrand = Build.BRAND,
                deviceName = Build.DEVICE,
                deviceManufacturer = Build.MANUFACTURER,
                deviceModel = Build.MODEL,
                deviceSdkInt = Build.VERSION.SDK_INT.toString(),
                batteryPercent = "87",

                /**Location**/
                latitude = 0.0,
                longitude = 0.0,

                /**Labels**/
                labels = hashMapOf(Pair("env", "dev"))
        ))
```

#### Enable MQTT Feature
_______________________________________________

**Note:** PLogger currently supports SSL connection for MQTT.

#### Step 1: 
Add certificate in your app's raw resource directory.

##### Step 2: 
Add following block for initializing MQTT logging.

```kotlin
        PLogMQTTProvider.initMQTTClient(applicationContext,
                topic = "YOUR_TOPIC",
                brokerUrl = "YOUR_URL", //Without Scheme
                certificateRes = R.raw.m2mqtt_ca,
                clientId = "5aa39cef4d544d658ecaf23db701099c",
                writeLogsToLocalStorage = true,
                initialDelaySecondsForPublishing = 30,
                debug = true
        )
```

That's it, MQTT setup is done.
_______________________________________________

##### ProGuard Rules: 

-keep class com.blackbox.plog.pLogs.models.** { *; }
-keepclassmembers class com.blackbox.plog.pLogs.models.** { *; }
-keep class com.blackbox.plog.** {*; }
-keep class com.blackbox.plog.pLogs.exporter.formatter.** {*; }
-keep class com.blackbox.plog.pLogs.exporter.** {*; }

Wiki
--------

Checkout [Wiki](https://github.com/umair13adil/RxLogs/wiki) for more information.

Change Log
----------
###### Version: 1.0.9
- Changed parameter name in LogsConfig from "enabled" to "enableLogsWriteToFile"

###### Version: 1.0.3
- Added MQTT Logging support

###### Version: 1.0.2
- Added ELK Logstash JSON logging support

###### Version: 0.23
- Added functionality to write logs in background thread.
- Fixed zip issues.
- Fixed issues with data formatting on decryption of text files.

###### Version: 0.21
- Removed **'context'** parameter in logs configuration
- Added **'exportFormatted'** parameter in logs configuration to enable **'HTML formatted Logs in case of exceptions'**

###### Version: 0.17
- **'autoClearLogs'** parameter in logs configuration changed to **'autoDeleteZipOnExport'**
- Added **'autoClearLogs'** parameter in logs configuration to enable/disable **'Auto Clear Logs'** feature
- Added **'context'** parameter in logs configuration to enable **'HTML formatted Logs in case of exceptions'**
- Added **'Log Events'** for formatted exceptions output
    * EventTypes.NEW_ERROR_REPORTED_FORMATTED
    * EventTypes.SEVERE_ERROR_REPORTED_FORMATTED
- Changed **'type: LogLevel'** parameter in **'logThis()'** function to **'level: LogLevel'**

## MIT License

##### Copyright (c) 2018 Muhammad Umair Adil

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
