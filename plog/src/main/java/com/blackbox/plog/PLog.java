package com.blackbox.plog;

/**
 * Created by Umair Adil on 12/04/2017.
 */

import android.util.Log;

import com.blackbox.plog.utils.DateControl;
import com.blackbox.plog.utils.DateTimeUtils;
import com.blackbox.plog.utils.Utils;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import de.mindpipe.android.logging.log4j.LogConfigurator;
import io.reactivex.Observable;

public class PLog {

    private static String TAG = PLog.class.getSimpleName();
    private final static LogConfigurator _logConfigurator = new LogConfigurator();

    private static String zipName = "";
    protected static PLogger pLogger = new PLogger();

    //Log Filters
    public static final int LOG_TODAY_ONLY = 0;
    public static final int LOG_TODAY = 1;
    public static final int LOG_LAST_HOUR = 2;
    public static final int LOG_WEEK = 3;
    public static final int LOG_LAST_TWO_DAYS = 4;

    //Log Types
    public static final String TYPE_INFO = "Info";
    public static final String TYPE_ERROR = "Error";
    public static final String TYPE_WARNING = "Warning";

    static void setPLogger(PLogger pLog) {
        pLogger = pLog;
    }

    /**
     * Configure Log4j
     *
     * @param fileName      Name of the log file
     * @param filePattern   Output format of the log line
     * @param maxBackupSize Maximum number of backed up log files
     * @param maxFileSize   Maximum size of log file until rolling
     */
    private static void Configure(String fileName, String filePattern,
                                  int maxBackupSize, long maxFileSize, boolean logEvent) {

        try {
            _logConfigurator.setFileName(fileName);
            _logConfigurator.setFilePattern(filePattern);
            _logConfigurator.setMaxBackupSize(maxBackupSize);
            _logConfigurator.setMaxFileSize(maxFileSize);
            _logConfigurator.setInternalDebugging(false);
            _logConfigurator.setUseLogCatAppender(logEvent);
            _logConfigurator.configure();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void logThis(String className, String functionName, String text, String type) {
        try {
            //Make sure what is logged is unique
            if (!Utils.getInstance().isLoggedOnce(text)) {


                String folderPath = getLogPath() + DateControl.getInstance().getToday();
                Utils.getInstance().createDirIfNotExists(folderPath);

                String fileName_raw = DateControl.getInstance().getToday() + DateControl.getInstance().getHour();
                String path_raw = folderPath + File.separator + fileName_raw;
                boolean existsRaw = Utils.getInstance().checkFileExists(path_raw);

                if (!existsRaw) {
                    String filePattern = "%m%n";
                    int maxBackupSize = 2500;
                    long maxFileSize = 2048 * 2048;
                    Configure(path_raw, filePattern, maxBackupSize, maxFileSize, true);
                }

                LogData logData = new LogData(className, functionName, text, DateTimeUtils.getFullDateTimeString(System.currentTimeMillis()), type);

                String logFormatted = LogFormatter.getFormatType(logData, pLogger);

                Logger.getLogger(TAG).info(logFormatted);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected static String getOutputPath() {
        return pLogger.getExportPath() + File.separator;
    }

    private static String getLogPath() {
        return pLogger.getSavePath() + File.separator;
    }

    public static Observable<String> getLogs(final int type) {

        return Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {

                FileFilter.prepareOutputFile();

                String path = "";
                int files = 0;
                String timeStamp = "";
                String noOfFiles = "";


                switch (type) {
                    case LOG_TODAY_ONLY:
                        path = getLogPath() + DateControl.getInstance().getToday();
                        files = FileFilter.getFilesForToday(path);

                        if (pLogger.getAttachTimeStamp())
                            timeStamp = "_" + DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis());

                        if (pLogger.getAttachNoOfFiles())
                            noOfFiles = "_[" + files + "]";

                        zipName = pLogger.getExportFileName() + timeStamp + noOfFiles + ".zip";
                        break;

                    case LOG_TODAY:

                        path = getLogPath() + DateControl.getInstance().getToday();
                        files = FileFilter.getFilesForToday(path);

                        if (pLogger.getAttachTimeStamp())
                            timeStamp = "_" + DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + "_Today";

                        if (pLogger.getAttachNoOfFiles())
                            noOfFiles = "_[" + files + "]";

                        zipName = pLogger.getExportFileName() + timeStamp + noOfFiles + ".zip";
                        break;

                    case LOG_LAST_HOUR:
                        path = getLogPath() + DateControl.getInstance().getToday();
                        FileFilter.getFilesForLastHour(path);

                        if (pLogger.getAttachTimeStamp())
                            timeStamp = "_" + DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + "_LastHour";

                        if (pLogger.getAttachNoOfFiles())
                            noOfFiles = "_[" + 1 + "]";

                        zipName = pLogger.getExportFileName() + timeStamp + noOfFiles + ".zip";
                        break;

                    case LOG_WEEK:
                        FileFilter.getFilesForLastWeek(getLogPath());

                        if (pLogger.getAttachTimeStamp())
                            timeStamp = "_" + DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + "_Week";

                        if (pLogger.getAttachNoOfFiles())
                            noOfFiles = "_[" + 1 + "]";

                        zipName = pLogger.getExportFileName() + timeStamp + noOfFiles + ".zip";
                        break;

                    case LOG_LAST_TWO_DAYS:
                        FileFilter.getFilesForLastTwoDays(getLogPath());

                        if (pLogger.getAttachTimeStamp())
                            timeStamp = "_" + DateTimeUtils.getFullDateTimeStringCompressed(System.currentTimeMillis()) + "_Last2Days";

                        if (pLogger.getAttachNoOfFiles())
                            noOfFiles = "_[" + 1 + "]";

                        zipName = pLogger.getExportFileName() + timeStamp + noOfFiles + ".zip";

                        break;

                }

                final String outputPath = getOutputPath();
                final File outputDirectory = new File(outputPath);
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
                        fos = new FileOutputStream(outputPath + zipName);

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
                    logThis(TAG, "getLogs", "Output Zip: " + outputPath + zipName, TYPE_INFO);

                return outputPath + zipName;
            }
        });

    }


    /**
     * Clear logs boolean.
     * <p>
     * <p>Will return true if delete was successful</p>
     *
     * @return the boolean
     */
    public static boolean clearLogs() {
        return Utils.getInstance().deleteDir(new File(getLogPath()));
    }
}
