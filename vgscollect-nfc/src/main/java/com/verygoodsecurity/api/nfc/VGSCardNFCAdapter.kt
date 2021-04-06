package com.verygoodsecurity.api.nfc

import android.app.Activity
import com.verygoodsecurity.vgscollect.app.VGSDataAdapter

abstract class VGSCardNFCAdapter : VGSDataAdapter() {

    abstract fun stopScanning()
    abstract fun startScanning()

    companion object {
        fun create(activity: Activity): VGSCardNFCAdapter {
            return VGSNFCModule(activity)
        }
    }
}