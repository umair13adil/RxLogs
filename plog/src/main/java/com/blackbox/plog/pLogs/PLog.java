package com.blackbox.plog.pLogs;

/**
 * Created by Umair Adil on 12/04/2017.
 */

import com.blackbox.plog.utils.DateControl;
import com.blackbox.plog.utils.DateTimeUtils;
import com.blackbox.plog.utils.Utils;

import org.apache.log4j.Logger;

import java.io.File;

import de.mindpipe.android.logging.log4j.LogConfigurator;
import io.reactivex.Observable;

public class PLog {

    private static String TAG = PLog.class.getSimpleName();
    private final static LogConfigurator _logConfigurator = new LogConfigurator();

    static PLogger pLogger = new PLogger();

    //Log Filters
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

    /**
     * Log this.
     * <p>
     * <p>Logs 'String' data along with class & function name to hourly based file with formatted timestamps.</p>
     *
     * @param className    the class name
     * @param functionName the function name
     * @param text         the text
     * @param type         the type
     */
    public static void logThis(String className, String functionName, String text, String type) {
        try {
            //Make sure what is logged is unique
            if (!Utils.getInstance().isLoggedOnce(text)) {

                String folderPath = getLogPath() + DateControl.getInstance().getToday();
                Utils.getInstance().createDirIfNotExists(folderPath);

                String fileName_raw = DateControl.getInstance().getToday() + DateControl.getInstance().getHour();
                String path_raw = folderPath + File.separator + fileName_raw + pLogger.getLogFileExtension();
                boolean existsRaw = Utils.getInstance().checkFileExists(path_raw);

                if (!existsRaw) {
                    String filePattern = "%m%n";
                    int maxBackupSize = 2500;
                    long maxFileSize = 2048 * 2048;
                    Configure(path_raw, filePattern, maxBackupSize, maxFileSize, !pLogger.isSilentLog());
                }

                LogData logData = new LogData(className, functionName, text, DateTimeUtils.getTimeFormatted(pLogger.getTimeStampFormat()), type);

                String logFormatted = LogFormatter.getFormatType(logData, pLogger);

                Logger.getLogger(TAG).info(logFormatted);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Gets output path.
     * <p>
     * <p>Sets the export path of Logs.</p>
     *
     * @return the output path
     */
    static String getOutputPath() {
        return pLogger.getExportPath() + File.separator;
    }

    /**
     * Gets Logs path.
     * <p>
     * <p>Sets the save path of Logs.</p>
     *
     * @return the save path
     */
    private static String getLogPath() {
        return pLogger.getSavePath() + File.separator;
    }

    /**
     * Gets logs.
     * <p>
     * <p>This will export logs based on filter type to export location with export name provided.</p>
     *
     * @param type the type
     * @return the logs
     */
    public static Observable<String> getLogs(final int type) {
        return LogExporter.getLogs(type, pLogger.getAttachTimeStamp(), pLogger.getAttachNoOfFiles(), getLogPath(), pLogger.getExportFileName(), getOutputPath());
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
