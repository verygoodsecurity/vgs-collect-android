package com.verygoodsecurity.vgscollect

import android.content.Context
import android.util.Log
import java.util.*


/**  */
object VGSCollectLogger {

    var logLevel: Level = Level.DEBUG
        @JvmName("setLogLevel") set
        @JvmName("getLogLevel") get

    enum class Level {
        DEBUG,
        WARN,
        NONE
    }

    fun isDebugEnabled(): Boolean = logLevel.ordinal != Level.NONE.ordinal

    private fun printLog(level: Level, tag: String, message: String) {
        if(level.ordinal >= logLevel.ordinal) {
            when (level) {
                Level.DEBUG -> Log.d(tag, message)
                Level.WARN -> Log.w(tag, message)
            }
        }
    }

    fun warn(tag: String, message: String) {
        printLog(Level.WARN, tag, message)
    }

    fun debug(tag: String, message: String) {
        printLog(Level.DEBUG, tag, message)
    }
}