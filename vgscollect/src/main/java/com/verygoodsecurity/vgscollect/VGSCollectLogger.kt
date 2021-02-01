package com.verygoodsecurity.vgscollect

import android.util.Log

/**
 * This object is used to log messages in VGS Collect SDK.
 */
object VGSCollectLogger {

    /** Current priority level for filtering debugging logs */
    var logLevel: Level = if(BuildConfig.DEBUG) Level.DEBUG else Level.NONE


    /**
     * Priority constant for the printing debug-logs.
     */
    enum class Level {

        /**
         * Default setting. We print all information about processing.
         * It includes errors, warnings, notifications, debug messages, requests and responses.
         */
        DEBUG,

        /**
         * This setting allows you to minimize information and print only errors, warnings.
         */
        WARN,

        /**
         * Disable debug-logs.
         */
        NONE
    }

    /**
     * Returns true if the logger print log messages.
     */
    fun isDebugEnabled(): Boolean = logLevel.ordinal != Level.NONE.ordinal

    private fun printLog(level: Level, tag: String, message: String) {
        if(level.ordinal >= logLevel.ordinal) {
            when (level) {
                Level.DEBUG -> Log.d(tag, message)
                Level.WARN -> Log.w(tag, message)
            }
        }
    }

    /**
     * Print a WARN log message.
     *
     * @param tag Used to identify the source of a log message.
     * @param message The message you would like logged.
     */
    internal fun warn(tag: String, message: String) {
        printLog(Level.WARN, tag, message)
    }

    /**
     * Print a DEBUG log message.
     *
     * @param tag Used to identify the source of a log message.
     * @param message The message you would like logged.
     */
    internal fun debug(tag: String, message: String) {
        printLog(Level.DEBUG, tag, message)
    }
}