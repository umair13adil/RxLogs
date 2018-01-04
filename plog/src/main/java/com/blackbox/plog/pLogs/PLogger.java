package com.blackbox.plog.pLogs;

import android.os.Environment;

import java.io.File;

/**
 * Created by umair on 03/01/2018.
 */

public class PLogger {

    private String TAG = PLogger.class.getSimpleName();

    private String savePath;
    private String exportPath;
    private String exportFileName;
    private Boolean attachTimeStamp;
    private Boolean attachNoOfFiles;
    private Boolean debug;
    private Boolean silentLogs;
    private String formatType = LogFormatter.FORMAT_CURLY;
    private String customFormatOpen;
    private String customFormatClose;
    private String csvDeliminator;
    private String timeStampFormat;
    private String logFileExtension;

    public PLogger() {

    }

    public PLogger(String savePath, String exportPath, String fileName, Boolean attachTimeStamp, Boolean attachNoOfFiles, String formatType, String customFormatOpen, String customFormatClose, String csvDeliminator, Boolean debug, String timeStampFormat, String logFileExtension, Boolean silentLogs) {
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
        this.timeStampFormat = timeStampFormat;
        this.logFileExtension = logFileExtension;
        this.silentLogs = silentLogs;
    }

    public String getSavePath() {
        if (savePath != null)
            return savePath;
        else
            return Environment.getExternalStorageDirectory() + File.separator + TAG;
    }

    public String getExportPath() {
        if (exportPath != null)
            return exportPath;
        else
            return Environment.getExternalStorageDirectory() + File.separator + TAG;
    }

    public String getExportFileName() {
        if (exportFileName != null)
            return exportFileName;
        else
            return "Output";
    }

    public Boolean getAttachTimeStamp() {
        if (attachTimeStamp != null)
            return attachTimeStamp;
        else
            return true;
    }

    public Boolean getAttachNoOfFiles() {
        if (attachNoOfFiles != null)
            return attachNoOfFiles;
        else
            return true;
    }

    public Boolean isDebuggable() {
        if (debug != null)
            return debug;
        else
            return false;
    }

    public Boolean isSilentLog() {
        if (silentLogs != null)
            return silentLogs;
        else
            return false;
    }

    public String getFormatType() {
        if (formatType != null)
            return formatType;
        else
            return LogFormatter.FORMAT_CURLY;
    }

    public String getCustomFormatOpen() {
        if (customFormatOpen != null)
            return customFormatOpen;
        else
            return " ";
    }

    public String getCustomFormatClose() {
        if (customFormatClose != null)
            return customFormatClose;
        else
            return " ";
    }

    public String getCsvDeliminator() {
        if (csvDeliminator != null)
            return csvDeliminator;
        else
            return ",";
    }

    public String getTimeStampFormat() {
        if (timeStampFormat != null)
            return timeStampFormat;
        else
            return "dd MMMM yyyy hh:mm:ss a";
    }

    public String getLogFileExtension() {
        if (logFileExtension != null)
            return logFileExtension;
        else
            return ".txt";
    }

}
