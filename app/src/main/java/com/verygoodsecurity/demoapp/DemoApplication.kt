package com.verygoodsecurity.demoapp

import android.app.Application
import android.os.StrictMode
import android.view.View
import com.google.android.gms.security.ProviderInstaller

class DemoApplication: Application() {
    val leakedViews = mutableListOf<View>()

    override fun onCreate() {
        super.onCreate()
        ProviderInstaller.installIfNeeded(getApplicationContext());
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