package com.blackbox.plog.pLogs

/**
 * Created by umair on 03/01/2018.
 */

object LogFormatter {

    //Format Types
    val FORMAT_CURLY = "1"
    val FORMAT_SQUARE = "2"
    val FORMAT_CSV = "3"
    val FORMAT_CUSTOM = "4"

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
            FORMAT_CURLY -> t = formatCurly(data)

            FORMAT_SQUARE -> t = formatSquare(data)

            FORMAT_CSV -> t = formatCSV(data, pLogger.csvDeliminator)

            FORMAT_CUSTOM -> t = formatCustom(data, pLogger.customFormatOpen, pLogger.customFormatClose)
        }

        return t
    }
}
