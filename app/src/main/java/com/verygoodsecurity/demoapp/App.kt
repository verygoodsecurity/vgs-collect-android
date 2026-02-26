@file:Suppress("SpellCheckingInspection", "RedundantOverride")

package com.verygoodsecurity.demoapp

import android.app.Application

class App: Application() {

    override fun onCreate() {
        super.onCreate()
//        MicroblinkSDK.setLicenseKey("<key>", this)
//        MicroblinkSDK.setLicenseKey("<path>", this)
    }
}