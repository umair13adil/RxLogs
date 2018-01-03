package com.blackbox.plog;

/**
 * Created by umair on 03/01/2018.
 */

public class PLogBuilder {

    private String savePath;
    private String exportPath;
    private String exportFileName;
    private Boolean attachTimeStamp;
    private Boolean attachNoOfFiles;
    private Boolean debug;
    private String formatType;
    private String customFormatOpen;
    private String customFormatClose;
    private String csvDeliminator;

    public PLogBuilder() {

    }

    public PLogBuilder setLogsSavePath(String path) {
        this.savePath = path;
        return this;
    }

    public PLogBuilder setLogsExportPath(String path) {
        this.exportPath = path;
        return this;
    }

    public PLogBuilder setExportFileName(String fileName) {
        this.exportFileName = fileName;
        return this;
    }

    public PLogBuilder attachTimeStampToFiles(boolean attach) {
        this.attachTimeStamp = attach;
        return this;
    }

    public PLogBuilder attachNoOfFilesToFiles(boolean attach) {
        this.attachNoOfFiles = attach;
        return this;
    }

    public PLogBuilder debuggable(boolean debug) {
        this.debug = debug;
        return this;
    }

    public PLogBuilder setLogFormatType(String type) {
        this.formatType = type;
        return this;
    }

    public PLogBuilder setCustomFormatOpen(String f) {
        this.customFormatOpen = f;
        return this;
    }

    public PLogBuilder setCSVDeliminator(String f) {
        this.csvDeliminator = f;
        return this;
    }

    public PLogBuilder setCustomFormatClose(String f) {
        this.customFormatClose = f;
        return this;
    }

    public PLogger build() {
        PLogger pLogger = new PLogger(savePath, exportPath, exportFileName, attachTimeStamp, attachNoOfFiles, formatType, customFormatOpen, customFormatClose, csvDeliminator, debug);
        PLog.setPLogger(pLogger);
        return pLogger;
    }
}
