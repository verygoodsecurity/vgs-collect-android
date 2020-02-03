package com.verygoodsecurity.demoapp

import android.app.Application
import android.os.StrictMode
import android.view.View

class DemoApplication: Application() {
    val leakedViews = mutableListOf<View>()

    override fun onCreate() {
        super.onCreate()
        enabledStrictMode()
    }

    private fun enabledStrictMode() {
//        StrictMode.setThreadPolicy(
//            StrictMode.ThreadPolicy.Builder()
//                .detectAll()
//                .penaltyLog()
//                .penaltyDeath()
//                .build()
//        )
    }
}