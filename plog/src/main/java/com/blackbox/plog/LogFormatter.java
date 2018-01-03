package com.blackbox.plog;

/**
 * Created by umair on 03/01/2018.
 */

public class LogFormatter {

    //Format Types
    public static final String FORMAT_CURLY = "1";
    public static final String FORMAT_SQUARE = "2";
    public static final String FORMAT_CSV = "3";
    public static final String FORMAT_CUSTOM = "4";

    private static String formatCurly(LogData data) {

        String SCREEN = data.getClassName();
        String FUNCTION = data.getFunctionName();
        String DATA = data.getLogText();
        String TIME = data.getLogTime();
        String TYPE = data.getLogType();

        return "{" + SCREEN + "}  {" + FUNCTION + "}  {" + DATA + "}  {" + TIME + "}  {" + TYPE + "}";
    }

    private static String formatSquare(LogData data) {

        String SCREEN = data.getClassName();
        String FUNCTION = data.getFunctionName();
        String DATA = data.getLogText();
        String TIME = data.getLogTime();
        String TYPE = data.getLogType();

        return "[" + SCREEN + "]  [" + FUNCTION + "]  [" + DATA + "]  {" +
                "[" + TIME + "]  [" + TYPE + "]";
    }

    private static String formatCSV(LogData data, String deliminator) {

        String SCREEN = data.getClassName();
        String FUNCTION = data.getFunctionName();
        String DATA = data.getLogText();
        String TIME = data.getLogTime();
        String TYPE = data.getLogType();

        return SCREEN + deliminator + FUNCTION + deliminator + DATA + deliminator + TIME + deliminator + TYPE;
    }

    private static String formatCustom(LogData data, String dividerOpen, String dividerClose) {

        String SCREEN = data.getClassName();
        String FUNCTION = data.getFunctionName();
        String DATA = data.getLogText();
        String TIME = data.getLogTime();
        String TYPE = data.getLogType();

        return dividerOpen + SCREEN + dividerClose + dividerOpen + FUNCTION + dividerClose + dividerOpen + DATA + dividerClose + dividerOpen +
                TIME + dividerClose + dividerOpen + TYPE + dividerClose;
    }

    static String getFormatType(LogData data, PLogger pLogger) {

        String t = "";

        switch (pLogger.getFormatType()) {
            case FORMAT_CURLY:
                t = formatCurly(data);
                break;

            case FORMAT_SQUARE:
                t = formatSquare(data);
                break;

            case FORMAT_CSV:
                t = formatCSV(data, pLogger.getCsvDeliminator());
                break;

            case FORMAT_CUSTOM:
                t = formatCustom(data, pLogger.getCustomFormatOpen(), pLogger.getCustomFormatClose());
                break;
        }

        return t;
    }
}
