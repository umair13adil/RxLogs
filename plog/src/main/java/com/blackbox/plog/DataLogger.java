package com.blackbox.plog;

import android.os.Environment;

import com.blackbox.plog.utils.DateControl;
import com.blackbox.plog.utils.Utils;

import java.io.File;
import java.io.RandomAccessFile;

import io.reactivex.Observable;

/**
 * Created by umair on 03/01/2018.
 */

public class DataLogger {

    private static String TAG = DataLogger.class.getSimpleName();

    private String savePath = Environment.getExternalStorageDirectory() + File.separator + TAG;
    private String exportPath = Environment.getExternalStorageDirectory() + File.separator + TAG;
    private String exportFileName = "Output";
    private String logFileName = "log";
    private Boolean attachTimeStamp = true;
    private Boolean debug = false;

    DataLogger(String savePath, String exportPath, String exportFileName, String logFileName, Boolean attachTimeStamp, Boolean debug) {
        this.savePath = savePath;
        this.exportPath = exportPath;
        this.exportFileName = exportFileName;
        this.logFileName = logFileName;
        this.attachTimeStamp = attachTimeStamp;
        this.debug = debug;
    }

    /**
     * Overwrite to file.
     * <p>
     * <p>This function will overwrite a 'String' data to a file.
     * File will be created if it doesn't exists in path provided.
     * Filename can contain extension as well e.g 'error_log.txt'.
     * If 'attachTimeStamp' is true filename will contain date & hour in it like: '0105201812_error_log.txt'.
     * Hours are in 24h format, so each file will be unique after an hour.
     * </p>
     *
     * @param dataToWrite the data to write can be any string data formatted or unformatted
     */
    public void overwriteToFile(String dataToWrite) {

        if (logFileName != null) {
            Utils.getInstance().createDirIfNotExists(getLogPath());

            String fileName_raw = "";

            if (attachTimeStamp)
                fileName_raw = DateControl.getInstance().getToday() + DateControl.getInstance().getHour() + "_" + logFileName;
            else
                fileName_raw = logFileName;

            String path_raw = getLogPath() + File.separator + fileName_raw;

            RandomAccessFile f = null;
            File file = new File(path_raw);
            try {
                f = new RandomAccessFile(file, "rw");
                f.setLength(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (f != null) {
                    f.write(dataToWrite.getBytes());
                    f.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            PLog.logThis(TAG, "overwriteToFile", "No File Name Provided!", PLog.TYPE_ERROR);
        }
    }

    /**
     * Append to file.
     * <p>
     * <p>This function will append a 'String' data to a file with new line inserted.
     * File will be created if it doesn't exists in path provided.
     * Filename can contain extension as well e.g 'error_log.txt'.
     * If 'attachTimeStamp' is true filename will contain date & hour in it like: '0105201812_error_log.txt'.
     * Hours are in 24h format, so each file will be unique after an hour.
     * </p>
     *
     * @param dataToWrite the data to write can be any string data formatted or unformatted
     */
    public void appendToFile(String dataToWrite) {

        if (logFileName != null) {
            try {

                Utils.getInstance().createDirIfNotExists(getLogPath());

                String fileName_raw = "";

                if (attachTimeStamp)
                    fileName_raw = DateControl.getInstance().getToday() + DateControl.getInstance().getHour() + "_" + logFileName;
                else
                    fileName_raw = logFileName;

                String path_raw = getLogPath() + File.separator + fileName_raw;

                RandomAccessFile f = null;
                File file = new File(path_raw);
                long fileLength = file.length();
                try {
                    f = new RandomAccessFile(file, "rw");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (f != null) {
                        f.seek(fileLength);
                        f.write(dataToWrite.getBytes());
                        f.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            PLog.logThis(TAG, "appendToFile", "No File Name Provided!", PLog.TYPE_ERROR);
        }
    }

    /**
     * Gets output path.
     * <p>
     * <p>Sets the export path of Logs.</p>
     *
     * @return the output path
     */
    private String getOutputPath() {
        return exportPath + File.separator;
    }

    /**
     * Gets Logs path.
     * <p>
     * <p>Sets the save path of Logs.</p>
     *
     * @return the save path
     */
    private String getLogPath() {
        return savePath + File.separator;
    }

    /**
     * Gets logs.
     * <p>
     * <p>This will export logs based on filter type to export location with export name provided.</p>
     *
     * @return the logs
     */
    public Observable<String> getLogs() {
        return LogExporter.getDataLogs(logFileName, attachTimeStamp, getLogPath(), exportFileName, getOutputPath(), debug);
    }

    /**
     * Clear logs boolean.
     * <p>
     * <p>Will return true if delete was successful</p>
     *
     * @return the boolean
     */
    public boolean clearLogs() {
        return Utils.getInstance().deleteDir(new File(getLogPath()));
    }
}
