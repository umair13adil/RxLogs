package com.blackbox.plog.dataLogs;

/**
 * Created by umair on 04/01/2018.
 */

public class DataLogBuilder {

    private String savePath;
    private String exportPath;
    private String logFileName;
    private String exportFileName;
    private Boolean attachTimeStamp;
    private Boolean debug;

    public DataLogBuilder() {

    }

    /**
     * Sets logs save path.
     * <p>
     * <p>Should be a String Name. Can have file separators.</p>
     *
     * @param path the path
     * @return the logs save path
     */
    public DataLogBuilder setLogsSavePath(String path) {
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
    public DataLogBuilder setLogsExportPath(String path) {
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
    public DataLogBuilder setExportFileName(String fileName) {
        this.exportFileName = fileName;
        return this;
    }

    /**
     * Sets log file name.
     * <p>
     * <p>Should be a String name. like 'location_logs.txt'
     * </p>
     *
     * @param fileName the file name
     * @return the log file name
     */
    public DataLogBuilder setLogFileName(String fileName) {
        this.logFileName = fileName;
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
    public DataLogBuilder attachTimeStampToFiles(boolean attach) {
        this.attachTimeStamp = attach;
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
    public DataLogBuilder debuggable(boolean debug) {
        this.debug = debug;
        return this;
    }

    public DataLogger build() {
        return new DataLogger(savePath, exportPath, exportFileName, logFileName, attachTimeStamp, debug);
    }
}
