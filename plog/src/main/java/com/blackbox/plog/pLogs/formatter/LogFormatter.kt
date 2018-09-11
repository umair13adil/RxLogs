package com.blackbox.plog.pLogs.formatter

import com.blackbox.plog.pLogs.PLog
import com.blackbox.plog.pLogs.models.LogData

/**
 * Created by umair on 03/01/2018.
 */

object LogFormatter {

    private fun formatCurly(data: LogData): String {

        val SCREEN = data.className
        val FUNCTION = data.functionName
        val DATA = data.logText
        val TIME = data.logTime
        val TYPE = data.logType

        return "{$SCREEN}  {$FUNCTION}  {$DATA}  {$TIME}  {$TYPE}\n"
    }

    private fun formatSquare(data: LogData): String {

        val SCREEN = data.className
        val FUNCTION = data.functionName
        val DATA = data.logText
        val TIME = data.logTime
        val TYPE = data.logType

        return "[" + SCREEN + "]  [" + FUNCTION + "]  [" + DATA + "]  {" +
                "[" + TIME + "]  [" + TYPE + "]\n"
    }

    private fun formatCSV(data: LogData, deliminator: String): String {

        val SCREEN = data.className
        val FUNCTION = data.functionName
        val DATA = data.logText
        val TIME = data.logTime
        val TYPE = data.logType

        return SCREEN + deliminator + FUNCTION + deliminator + DATA + deliminator + TIME + deliminator + TYPE + "\n"
    }

    private fun formatCustom(data: LogData, dividerOpen: String, dividerClose: String): String {

        val SCREEN = data.className
        val FUNCTION = data.functionName
        val DATA = data.logText
        val TIME = data.logTime
        val TYPE = data.logType

        return dividerOpen + SCREEN + dividerClose + dividerOpen + FUNCTION + dividerClose + dividerOpen + DATA + dividerClose + dividerOpen +
                TIME + dividerClose + dividerOpen + TYPE + dividerClose + "\n"
    }

    internal fun getFormatType(data: LogData): String {

        var t = ""
        val providedFormat = PLog.getLogsConfig()?.formatType!!

        t = when (providedFormat) {
            FormatType.FORMAT_CURLY -> formatCurly(data)

            FormatType.FORMAT_SQUARE -> formatSquare(data)

            FormatType.FORMAT_CSV -> formatCSV(data, PLog.getLogsConfig()?.csvDeliminator!!)

            FormatType.FORMAT_CUSTOM -> formatCustom(data, PLog.getLogsConfig()?.customFormatOpen!!, PLog.getLogsConfig()?.customFormatClose!!)
        }

        return t
    }
}
