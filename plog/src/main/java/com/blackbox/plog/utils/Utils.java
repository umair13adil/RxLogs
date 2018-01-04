package com.blackbox.plog.utils;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Umair Adil on 18/11/2016.
 */
public class Utils {

    private String TAG = Utils.class.getSimpleName();
    private static Utils ourInstance = new Utils();

    private static String loggedItem = "";

    public static Utils getInstance() {
        return ourInstance;
    }

    public Utils() {

    }

    public void createDirIfNotExists(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public boolean checkFileExists(String path) {
        File file = new File(Environment.getExternalStorageDirectory(), path);
        return file.exists();
    }

    public void copyFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + File.separator + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

            new File(inputPath + inputFile).delete();

        } catch (FileNotFoundException fnfe1) {
            Log.e(TAG, fnfe1.getMessage());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }


    public boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
    }


    public boolean isLoggedOnce(String logString) {
        Boolean logged = false;

        if (!TextUtils.isEmpty(logString) && !TextUtils.isEmpty(loggedItem)) {
            if (logString.equals(loggedItem))
                logged = true;
        }

        loggedItem = logString;
        return logged;
    }

}
