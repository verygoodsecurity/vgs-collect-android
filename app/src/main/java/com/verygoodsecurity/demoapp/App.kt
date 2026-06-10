@file:Suppress("SpellCheckingInspection", "RedundantOverride")

package com.verygoodsecurity.demoapp

import android.app.Application

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        // BlinkCard v3000: license key is now passed per-scan via VGSBlinkCardIntentBuilder.setLicenseKey("<key>").
        // Global MicroblinkSDK.setLicenseKey() is no longer used.
    }
}