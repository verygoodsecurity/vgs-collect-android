package com.verygoodsecurity.demoapp

import android.app.Application
import com.microblink.blinkcard.MicroblinkSDK

class VGSSampleApplication: Application() {

    override fun onCreate() {
        super.onCreate()
//        MicroblinkSDK.setLicenseKey("<key>", this)
//        MicroblinkSDK.setLicenseKey("<path>", this)
    }
}