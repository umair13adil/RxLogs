package com.blackbox.plog.pLogs.formatter

import com.blackbox.plog.pLogs.models.LogData
import com.blackbox.plog.pLogs.models.PLogger

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

    internal fun getFormatType(data: LogData, pLogger: PLogger): String {

        var t = ""

        when (pLogger.formatType) {
            FormatType.FORMAT_CURLY -> t = formatCurly(data)

            FormatType.FORMAT_SQUARE -> t = formatSquare(data)

            FormatType.FORMAT_CSV -> t = formatCSV(data, pLogger.csvDeliminator)

            FormatType.FORMAT_CUSTOM -> t = formatCustom(data, pLogger.customFormatOpen, pLogger.customFormatClose)
        }

        return t
    }
}
