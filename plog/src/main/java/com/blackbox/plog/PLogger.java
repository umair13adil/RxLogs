package com.blackbox.plog;

import android.os.Environment;

import java.io.File;

/**
 * Created by umair on 03/01/2018.
 */

public class PLogger {

    private String TAG = PLogger.class.getSimpleName();

    private String savePath = Environment.getExternalStorageDirectory() + File.separator + TAG;
    private String exportPath = Environment.getExternalStorageDirectory() + File.separator + TAG;
    private String exportFileName = "Output";
    private Boolean attachTimeStamp = true;
    private Boolean attachNoOfFiles = true;
    private Boolean debug = true;
    private String formatType = LogFormatter.FORMAT_CURLY;
    private String customFormatOpen = "";
    private String customFormatClose = "";
    private String csvDeliminator = ",";

    public PLogger() {

    }

    public PLogger(String savePath, String exportPath, String fileName, Boolean attachTimeStamp, Boolean attachNoOfFiles, String formatType, String customFormatOpen, String customFormatClose, String csvDeliminator, Boolean debug) {
        this.savePath = savePath;
        this.exportPath = exportPath;
        this.exportFileName = fileName;
        this.attachTimeStamp = attachTimeStamp;
        this.attachNoOfFiles = attachNoOfFiles;
        this.formatType = formatType;
        this.customFormatOpen = customFormatOpen;
        this.customFormatClose = customFormatClose;
        this.csvDeliminator = csvDeliminator;
        this.debug = debug;
    }

    public String getSavePath() {
        return savePath;
    }

    public String getExportPath() {
        return exportPath;
    }

    public String getExportFileName() {
        return exportFileName;
    }

    public Boolean getAttachTimeStamp() {
        return attachTimeStamp;
    }

    public Boolean getAttachNoOfFiles() {
        return attachNoOfFiles;
    }

    public Boolean isDebuggable() {
        return debug;
    }

    public String getFormatType() {
        return formatType;
    }

    public String getCustomFormatOpen() {
        return customFormatOpen;
    }

    public String getCustomFormatClose() {
        return customFormatClose;
    }

    public String getCsvDeliminator() {
        return csvDeliminator;
    }

}
