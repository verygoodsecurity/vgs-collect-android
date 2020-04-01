package com.verygoodsecurity.vgscollect.util

import android.content.Context
import android.util.Log
import com.verygoodsecurity.vgscollect.BuildConfig

/** @suppress */
internal object Logger {
    fun e(context: Context, clazz: Class<*>, resId:Int) {
        if (BuildConfig.DEBUG) {
            val message = context.getString(resId)
            Log.e(clazz.canonicalName, message)
        }
    }

    fun e(clazz: Class<*>, message:String) {
        if (BuildConfig.DEBUG) {
            Log.e(clazz.canonicalName, message)
        }
    }

    fun e(tag:String, message:String) {
        if(BuildConfig.DEBUG) {
            Log.e(tag, message)
        }
    }

    fun i(tag:String, message:String) {
        if(BuildConfig.DEBUG) {
            Log.i(tag, message)
        }
    }
}