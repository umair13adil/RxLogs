package com.blackbox.plog.utils;

/**
 * Created by umair on 15/05/2017.
 */

import android.util.Log;

import com.blackbox.plog.pLogs.PLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import io.reactivex.Observable;

public class Compress {

    private String TAG = Compress.class.getSimpleName();

    private File[] _files;
    private String _zipFile;
    private String outputPath;

    public Compress(File[] files, String outPath, String zipFile) {
        _files = files;
        _zipFile = zipFile;
        outputPath = outPath;
    }


    public Observable<String> zip() {

        return Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {

                try {
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(outputPath + _zipFile);

                        ZipOutputStream zos = new ZipOutputStream(fos);

                        for (int i = 0; i < _files.length; i++) {
                            if (!_files[i].getName().contains(".zip")) {

                                File f = _files[i];

                                PLog.logThis(TAG, "zip", "Adding file: " + f.getName(), PLog.TYPE_INFO);
                                byte[] buffer = new byte[1024];
                                FileInputStream fis = new FileInputStream(f);
                                zos.putNextEntry(new ZipEntry(f.getName()+".txt"));
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
                        PLog.logThis(TAG, "zip", e.getMessage(), PLog.TYPE_ERROR);
                    }
                } catch (IOException ioe) {
                    Log.e(TAG, ioe.getMessage());
                    PLog.logThis(TAG, "zip", ioe.getMessage(), PLog.TYPE_ERROR);
                }

                return "Zipped";
            }
        });

    }

}
