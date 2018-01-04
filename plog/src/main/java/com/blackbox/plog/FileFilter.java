package com.blackbox.plog;

import android.util.Log;

import com.blackbox.plog.utils.DateControl;
import com.blackbox.plog.utils.Utils;

import java.io.File;

/**
 * Created by umair on 03/01/2018.
 */
class FileFilter {

    private static String TAG = FileFilter.class.getSimpleName();

    static void getFilesForLastHour(String folderPath) {

        File directory = new File(folderPath);
        File[] files = directory.listFiles();

        int lastHour = Integer.parseInt(DateControl.getInstance().getHour()) - 1;

        if (files.length > 0) {

            boolean found = filterFile(folderPath, files, lastHour);

            if (!found) {
                lastHour = Integer.parseInt(DateControl.getInstance().getHour());
                filterFile(folderPath, files, lastHour);
            }

        }
    }

    private static boolean filterFile(String folderPath, File[] files, int lastHour) {
        boolean found = false;

        for (int i = 0; i < files.length; i++) {
            int fileHour = extractHour(files[i].getName());

            if (PLog.pLogger.isDebuggable())
                Log.i(TAG, "Last Hour: " + lastHour + " Check File Hour: " + fileHour + " " + files[i].getName());

            if (fileHour == lastHour) {
                found = true;
                Utils.getInstance().copyFile(folderPath, files[i].getName(), PLog.getOutputPath());
            }
        }

        return found;
    }

    private static int extractDay(String name) {
        return Integer.parseInt(name.substring(0, 2));
    }

    private static int extractHour(String name) {
        return Integer.parseInt(name.substring(8, 10));
    }

    static void getFilesForLastWeek(String folderPath) {

        int today = Integer.parseInt(DateControl.getInstance().getCurrentDate());
        int lastWeek = Integer.parseInt(DateControl.getInstance().getLastWeek());

        File directory = new File(folderPath);
        final File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file != null) {
                    if (file.isDirectory()) {
                        int day = extractDay(file.getName());

                        if (PLog.pLogger.isDebuggable())
                            Log.i(TAG, "Files between dates: " + lastWeek + " & " + today + ",Date File Present: " + day);

                        if (lastWeek < today) {
                            if (day <= today && day >= lastWeek) {
                                getFilesForToday(file.getPath());
                            }
                        } else {
                            if (day <= today) {
                                getFilesForToday(file.getPath());
                            }
                        }
                    }
                }
            }
        }
    }

    static void getFilesForLastTwoDays(String folderPath) {

        int today = Integer.parseInt(DateControl.getInstance().getCurrentDate());
        int lastDay = Integer.parseInt(DateControl.getInstance().getLastDay());

        File directory = new File(folderPath);
        final File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file != null) {
                    if (file.isDirectory()) {
                        int day = extractDay(file.getName());

                        if (PLog.pLogger.isDebuggable())
                            Log.i(TAG, "Files between dates: " + lastDay + " & " + today + ",Date File Present: " + day);

                        if (lastDay < today) {
                            if (day <= today && day >= lastDay) {
                                getFilesForToday(file.getPath());
                            }
                        } else {
                            if (day <= today) {
                                getFilesForToday(file.getPath());
                            }
                        }
                    }
                }
            }
        }
    }

    static void prepareOutputFile(String outputPath) {
        Utils.getInstance().deleteDir(new File(outputPath));
        Utils.getInstance().createDirIfNotExists(outputPath);
    }


    static int getFilesForToday(String folderPath) {

        int size = 0;

        String outputPath = PLog.getOutputPath();
        File directory = new File(folderPath);
        File[] files = directory.listFiles();

        if (files != null && files.length > 0) {

            size = files.length;

            if (PLog.pLogger.isDebuggable())
                Log.i(TAG, "Total Files: " + size);

            if (files.length > 0) {

                for (int i = 0; i < files.length; i++) {
                    Utils.getInstance().copyFile(folderPath, files[i].getName(), outputPath);
                }
            }
        }

        return size;
    }

    static int getFilesForLogName(String logsPath, String outputPath, String logFileName, boolean debug) {

        int size = 0;

        File directory = new File(logsPath);
        File[] files = directory.listFiles();

        if (files != null && files.length > 0) {

            size = files.length;

            if (debug)
                Log.i(TAG, "Total Files: " + size);

            if (files.length > 0) {

                for (int i = 0; i < files.length; i++) {
                    if (files[i].getName().contains(logFileName))
                        Utils.getInstance().copyFile(logsPath, files[i].getName(), outputPath);
                }
            }
        }

        return size;
    }

}
