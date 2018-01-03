package com.blackbox.plog;

import com.blackbox.plog.utils.DateControl;
import com.blackbox.plog.utils.Utils;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * Created by umair on 03/01/2018.
 */

public class FileLogger {

    private String TAG = FileLogger.class.getSimpleName();

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
     * @param folderPath      the folder path must be absolute path to file in storage
     * @param fileName        the file name can be name with or without extension
     * @param attachTimeStamp the attach time stamp
     * @param dataToWrite     the data to write can be any string data formatted or unformatted
     */
    public static void overwriteToFile(String folderPath, String fileName, boolean attachTimeStamp, String dataToWrite) {

        Utils.getInstance().createDirIfNotExists(folderPath);

        String fileName_raw = "";

        if (attachTimeStamp)
            fileName_raw = DateControl.getInstance().getToday() + DateControl.getInstance().getHour() + "_" + fileName;
        else
            fileName_raw = fileName;

        String path_raw = folderPath + File.separator + fileName_raw;

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
     * @param folderPath      the folder path must be absolute path to file in storage
     * @param fileName        the file name can be name with or without extension
     * @param attachTimeStamp the attach time stamp
     * @param dataToWrite     the data to write can be any string data formatted or unformatted
     */
    public static void appendToFile(String folderPath, String fileName, boolean attachTimeStamp, String dataToWrite) {

        try {

            Utils.getInstance().createDirIfNotExists(folderPath);

            String fileName_raw = "";

            if (attachTimeStamp)
                fileName_raw = DateControl.getInstance().getToday() + DateControl.getInstance().getHour() + "_" + fileName;
            else
                fileName_raw = fileName;

            String path_raw = folderPath + File.separator + fileName_raw;

            RandomAccessFile f = null;
            File file = new File(path_raw);
            long fileLength = file.length();
            try {
                f = new RandomAccessFile(file, "rw");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if(f!=null) {
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
    }
}
