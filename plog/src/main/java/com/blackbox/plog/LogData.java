package com.blackbox.plog;

/**
 * Created by umair on 03/01/2018.
 */

public class LogData {

    private String className;
    private String functionName;
    private String logText;
    private String logTime;
    private String logType;

    public LogData(String className, String functionName, String logText, String logTime, String logType) {
        this.className = className;
        this.functionName = functionName;
        this.logText = logText;
        this.logTime = logTime;
        this.logType = logType;
    }

    protected String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    protected String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    protected String getLogText() {
        return logText;
    }

    public void setLogText(String logText) {
        this.logText = logText;
    }

    protected String getLogTime() {
        return logTime;
    }

    public void setLogTime(String logTime) {
        this.logTime = logTime;
    }

    protected String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }
}
