package com.blackbox.plog;

/**
 * Created by umair on 03/01/2018.
 */

public class PLogBuilder {

    private String savePath;
    private String exportPath;
    private String logFileExtension;
    private String exportFileName;
    private Boolean attachTimeStamp;
    private Boolean attachNoOfFiles;
    private Boolean debug;
    private Boolean silentLogs;
    private String formatType;
    private String customFormatOpen;
    private String customFormatClose;
    private String csvDeliminator;
    private String timeStampFormat;

    public PLogBuilder() {

    }

    /**
     * Sets logs save path.
     * <p>
     * <p>Should be a String Name. Can have file separators.</p>
     *
     * @param path the path
     * @return the logs save path
     */
    public PLogBuilder setLogsSavePath(String path) {
        this.savePath = path;
        return this;
    }

    /**
     * Sets logs export path.
     * <p>
     * <p>Should be a String Name. Can have file separators.</p>
     *
     * @param path the path
     * @return the logs export path
     */
    public PLogBuilder setLogsExportPath(String path) {
        this.exportPath = path;
        return this;
    }

    /**
     * Sets export file name.
     * <p>
     * <p>Should be a String name. like 'output_logs'
     * Don't add any extensions.
     * Default extension will be '.zip'.
     * </p>
     *
     * @param fileName the file name
     * @return the export file name
     */
    public PLogBuilder setExportFileName(String fileName) {
        this.exportFileName = fileName;
        return this;
    }

    /**
     * Attach time stamp to files log builder.
     * <p>
     * <p>Will attach time stamps to log and zip files.</p>
     *
     * @param attach the attach
     * @return the p log builder
     */
    public PLogBuilder attachTimeStampToFiles(boolean attach) {
        this.attachTimeStamp = attach;
        return this;
    }

    /**
     * Attach no of files to files p log builder.
     * <p>
     * <p>Will attach no of files zipped to export file name.</p>
     *
     * @param attach the attach
     * @return the p log builder
     */
    public PLogBuilder attachNoOfFilesToFiles(boolean attach) {
        this.attachNoOfFiles = attach;
        return this;
    }

    /**
     * Log silently p log builder.
     * <p>
     * <p>If true will not print logs in LogCat.
     * It is true by default.</p>
     *
     * @param silentLogs the silent logs
     * @return the p log builder
     */
    public PLogBuilder logSilently(boolean silentLogs) {
        this.silentLogs = silentLogs;
        return this;
    }

    /**
     * Debuggable p log builder.
     * <p>
     * <p>Prints debug logs.
     * Is true by default.</p>
     *
     * @param debug the debug
     * @return the p log builder
     */
    public PLogBuilder debuggable(boolean debug) {
        this.debug = debug;
        return this;
    }

    /**
     * Sets log format type.
     * <p>
     * <p>Sets the default format type.
     * Can choose from 'LogFormatter' class:
     * Following types are available:
     * <p>
     * 1. LogFormatter.FORMAT_CURLY
     * 2. LogFormatter.FORMAT_SQUARE
     * 3. LogFormatter.FORMAT_CSV
     * 4. LogFormatter.FORMAT_CUSTOM
     * <p>
     * CSV format will require a deliminator, by default it is comma ','.
     * Custom formats will require opening & closing characters. Like {},[], ' ' etc
     * </p>
     *
     * @param type the type
     * @return the log format type
     */
    public PLogBuilder setLogFormatType(String type) {
        this.formatType = type;
        return this;
    }

    /**
     * Sets custom format open.
     * <p>
     * <p>Could be any character like: '{' , '[' etc</p>
     *
     * @param f the f
     * @return the custom format open
     */
    public PLogBuilder setCustomFormatOpen(String f) {
        this.customFormatOpen = f;
        return this;
    }

    /**
     * Sets custom format close.
     * <p>
     * <p>Could be any character like: '}' , ']' etc</p>
     *
     * @param f the f
     * @return the custom format close
     */
    public PLogBuilder setCustomFormatClose(String f) {
        this.customFormatClose = f;
        return this;
    }

    /**
     * Sets csv deliminator.
     * <p>
     * <p>Could be any deliminator like: ';' , ':' etc</p>
     *
     * @param f the f
     * @return the csv deliminator
     */
    public PLogBuilder setCSVDeliminator(String f) {
        this.csvDeliminator = f;
        return this;
    }

    /**
     * Sets time stamp format.
     * <p>
     * Sets default time stamp format of logs.
     * e.g DDMMYYY
     *
     * @param f the f
     * @return the time stamp format
     */
    public PLogBuilder setTimeStampFormat(String f) {
        this.timeStampFormat = f;
        return this;
    }

    /**
     * Sets log file extension.
     * <p>
     * By default extension is '.txt'.
     * If you don't want any extensions in logs then pass empty string to this value.
     *
     * @param f the f
     * @return the log file extension
     */
    public PLogBuilder setLogFileExtension(String f) {
        this.logFileExtension = f;
        return this;
    }

    public PLogger build() {
        PLogger pLogger = new PLogger(savePath, exportPath, exportFileName, attachTimeStamp, attachNoOfFiles, formatType, customFormatOpen, customFormatClose, csvDeliminator, debug, timeStampFormat, logFileExtension,silentLogs);
        PLog.setPLogger(pLogger);
        return pLogger;
    }
}
