package com.verygoodsecurity.vgscollect

import android.content.Context
import android.util.Log
import java.util.*


/**  */
object VGSLogger {

    var logLevel: Level = Level.DEBUG
        @JvmName("setLogLevel") set
        @JvmName("getLogLevel") get

    enum class Level {
        DEBUG,
        WARN,
        NONE
    }

    fun isDebugEnabled(): Boolean = logLevel.ordinal < Level.NONE.ordinal

    private fun printLog(level: Level, tag: String, message: String) {
        if(level.ordinal >= logLevel.ordinal) {
            when (level) {
                Level.DEBUG -> Log.d(tag, message)
                Level.WARN -> Log.e(tag, message)
            }
        }
    }

    fun warn(tag: String, message: String) {
        printLog(Level.WARN, tag, message)
    }

    fun debug(tag: String, message: String) {
        printLog(Level.DEBUG, tag, message)
    }
//
//    fun e(
//        context: Context,
//        clazz: Class<*>,
//        resId: Int,
//        params: String
//    ) {
////        if (level.ordinal >= Level.DEBUG) {
//            val message = context.getString(resId, params)
//            Log.e(clazz.canonicalName, message)
////        }
//    }
//
//    fun e(context: Context, clazz: Class<*>, resId: Int) {
//        if (BuildConfig.DEBUG) {
//            val message = context.getString(resId)
//            Log.e(clazz.canonicalName, message)
//        }
//    }
//
//    fun e(clazz: Class<*>, message: String) {
//        if (BuildConfig.DEBUG) {
//            Log.e(clazz.canonicalName, message)
//        }
//    }
//
//    fun e(tag: String, message: String) {
//        if (BuildConfig.DEBUG) {
//            Log.e(tag, message)
//        }
//    }
//
//    fun w(clazz: Class<*>, message: String) {
//        if (BuildConfig.DEBUG) {
//            Log.w(clazz.canonicalName, message)
//        }
//    }
//
//    fun i(tag: String, message: String) {
//        if (BuildConfig.DEBUG) {
//            Log.i(tag, message)
//        }
//    }
//
//    fun d(clazz: Class<*>, message: String) {
//        if (BuildConfig.DEBUG) {
//            Log.w(clazz.canonicalName, message)
//        }
//    }
}