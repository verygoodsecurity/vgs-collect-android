package com.verygoodsecurity.vgscollect.util

import android.util.Log
import com.verygoodsecurity.vgscollect.BuildConfig

object Logger {
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