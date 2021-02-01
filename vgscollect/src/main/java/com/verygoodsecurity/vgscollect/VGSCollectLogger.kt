package com.verygoodsecurity.vgscollect

import android.util.Log
import com.verygoodsecurity.vgscollect.VGSCollectLogger.Level.*

/**
 * This object is used to log messages in VGS Collect SDK.
 */
object VGSCollectLogger {

    private const val TAG = "VGSCollect"

    /** Current priority level for filtering debugging logs */
    var logLevel: Level = if(BuildConfig.DEBUG) DEBUG else NONE

    /** Allows enable and disable debug-log printing. */
    var isEnabled = BuildConfig.DEBUG

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
    fun isDebugEnabled(): Boolean = logLevel.ordinal != NONE.ordinal

    private fun printLog(level: Level, tag: String?, message: String) {
        if(isEnabled && level.ordinal >= logLevel.ordinal) {
            val log:String = if(tag.isNullOrEmpty()) message else "$tag: $message"

            when (level) {
                DEBUG -> Log.d(TAG, log)
                WARN -> Log.w(TAG, log)
            }
        }
    }

    /**
     * Print a WARN log message.
     *
     * @param tag Used to identify the source of a log message.
     * @param message The message you would like logged.
     */
    internal fun warn(tag: String? = null, message: String) {
        printLog(WARN, tag, message)
    }

    /**
     * Print a DEBUG log message.
     *
     * @param tag Used to identify the source of a log message.
     * @param message The message you would like logged.
     */
    internal fun debug(tag: String? = null, message: String) {
        printLog(DEBUG, tag, message)
    }
}