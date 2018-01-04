package com.blackbox.plog.pLogs;

import android.util.Log;

import com.blackbox.plog.utils.DateControl;
import com.blackbox.plog.utils.DateTimeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import io.reactivex.Observable;

import static com.blackbox.plog.pLogs.PLog.LOG_LAST_HOUR;
import static com.blackbox.plog.pLogs.PLog.LOG_LAST_TWO_DAYS;
import static com.blackbox.plog.pLogs.PLog.LOG_TODAY;
import static com.blackbox.plog.pLogs.PLog.LOG_WEEK;
import static com.blackbox.plog.pLogs.PLog.TYPE_ERROR;
import static com.blackbox.plog.pLogs.PLog.TYPE_INFO;
import static com.blackbox.plog.pLogs.PLog.TYPE_WARNING;
import static com.blackbox.plog.pLogs.PLog.logThis;
import static com.blackbox.plog.pLogs.PLog.pLogger;

/**
 * Created by umair on 04/01/2018.
 */

public class LogExporter {

    private static String TAG = LogExporter.class.getSimpleName();

    private static String zipName = "";

    public static Observable<String> getLogs(final int type, final boolean attachTimeStamp, final boolean attachNoOfFiles, final String logPath, final String exportFileName, final String exportPath) {

        return Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {

                FileFilter.prepareOutputFile(exportPath);

                String path = "";
                int files = 0;
                String timeStamp = "";
                String noOfFiles = "";


                switch (type) {

                    case LOG_TODAY:

                        path = logPath + DateControl.getInstance().getToday();
                        files = FileFilter.getFilesForToday(path);

                        if (attachTimeStamp)
                            timeStamp = "_" + DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + "_Today";

                        if (attachNoOfFiles)
                            noOfFiles = "_[" + files + "]";

                        zipName = exportFileName + timeStamp + noOfFiles + ".zip";
                        break;

                    case LOG_LAST_HOUR:
                        path = logPath + DateControl.getInstance().getToday();
                        FileFilter.getFilesForLastHour(path);

                        if (attachTimeStamp)
                            timeStamp = "_" + DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + "_LastHour";

                        if (attachNoOfFiles)
                            noOfFiles = "_[" + 1 + "]";

                        zipName = exportFileName + timeStamp + noOfFiles + ".zip";
                        break;

                    case LOG_WEEK:
                        FileFilter.getFilesForLastWeek(logPath);

                        if (attachTimeStamp)
                            timeStamp = "_" + DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + "_Week";

                        if (attachNoOfFiles)
                            noOfFiles = "_[" + 1 + "]";

                        zipName = exportFileName + timeStamp + noOfFiles + ".zip";
                        break;

                    case LOG_LAST_TWO_DAYS:
                        FileFilter.getFilesForLastTwoDays(logPath);

                        if (attachTimeStamp)
                            timeStamp = "_" + DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + "_Last2Days";

                        if (attachNoOfFiles)
                            noOfFiles = "_[" + 1 + "]";

                        zipName = exportFileName + timeStamp + noOfFiles + ".zip";

                        break;

                }

                final File outputDirectory = new File(exportPath);
                final File[] filesToSend = outputDirectory.listFiles();

                if (filesToSend != null && filesToSend.length > 0) {
                    if (pLogger.isDebuggable())
                        logThis(TAG, "createZipFile", "Start Zipping Log Files.. " + filesToSend.length, TYPE_INFO);
                } else {
                    if (pLogger.isDebuggable())
                        logThis(TAG, "createZipFile", "No Files to zip!", TYPE_WARNING);
                    return null;
                }

                try {
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(exportPath + zipName);

                        ZipOutputStream zos = new ZipOutputStream(fos);

                        for (File f : filesToSend) {
                            if (!f.getName().contains(".zip")) {

                                if (pLogger.isDebuggable())
                                    logThis(TAG, "zipFile", "Adding file: " + f.getName(), TYPE_INFO);

                                byte[] buffer = new byte[1024];
                                FileInputStream fis = new FileInputStream(f);
                                zos.putNextEntry(new ZipEntry(f.getName() + ".txt"));
                                int length;
                                while ((length = fis.read(buffer)) > 0) {
                                    zos.write(buffer, 0, length);
                                }
                                zos.closeEntry();
                                fis.close();
                            }
                        }

                        zos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        if (pLogger.isDebuggable())
                            logThis(TAG, "getLogs", "Error: " + e.getLocalizedMessage(), TYPE_ERROR);
                        return null;
                    }
                } catch (IOException ioe) {
                    Log.e(TAG, ioe.getMessage());
                    if (pLogger.isDebuggable())
                        logThis(TAG, "getLogs", "Error: " + ioe.getLocalizedMessage(), TYPE_ERROR);
                    return null;
                }

                if (pLogger.isDebuggable())
                    logThis(TAG, "getLogs", "Output Zip: " + exportPath + zipName, TYPE_INFO);

                return exportPath + zipName;
            }
        });

    }

    public static Observable<String> getDataLogs(final String logFileName, final boolean attachTimeStamp, final String logPath, final String exportFileName, final String exportPath, final boolean debug) {

        return Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {

                FileFilter.prepareOutputFile(exportPath);

                String timeStamp = "";
                String noOfFiles = "";

                FileFilter.getFilesForLogName(logPath, exportPath, logFileName, debug);

                if (attachTimeStamp)
                    timeStamp = "_" + DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis());

                String zipName = exportFileName + timeStamp + noOfFiles + ".zip";

                final File outputDirectory = new File(exportPath);
                final File[] filesToSend = outputDirectory.listFiles();

                if (filesToSend != null && filesToSend.length > 0) {
                    if (debug)
                        logThis(TAG, "createZipFile", "Start Zipping Log Files.. " + filesToSend.length, TYPE_INFO);
                } else {
                    if (debug)
                        logThis(TAG, "createZipFile", "No Files to zip!", TYPE_WARNING);
                    return null;
                }

                try {
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(exportPath + zipName);

                        ZipOutputStream zos = new ZipOutputStream(fos);

                        for (File f : filesToSend) {
                            if (!f.getName().contains(".zip")) {

                                if (debug)
                                    logThis(TAG, "zipFile", "Adding file: " + f.getName(), TYPE_INFO);

                                byte[] buffer = new byte[1024];
                                FileInputStream fis = new FileInputStream(f);
                                zos.putNextEntry(new ZipEntry(f.getName() + ".txt"));
                                int length;
                                while ((length = fis.read(buffer)) > 0) {
                                    zos.write(buffer, 0, length);
                                }
                                zos.closeEntry();
                                fis.close();
                            }
                        }

                        zos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        if (debug)
                            logThis(TAG, "getDataLogs", "Error: " + e.getLocalizedMessage(), TYPE_ERROR);
                        return null;
                    }
                } catch (IOException ioe) {
                    Log.e(TAG, ioe.getMessage());
                    if (debug)
                        logThis(TAG, "getDataLogs", "Error: " + ioe.getLocalizedMessage(), TYPE_ERROR);
                    return null;
                }

                if (debug)
                    logThis(TAG, "getDataLogs", "Output Zip: " + exportPath + zipName, TYPE_INFO);

                return exportPath + zipName;
            }
        });

    }

}
