package com.verygoodsecurity.api.nfc

import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.verygoodsecurity.vgscollect.app.VGSDataAdapter

abstract class NFCAdapter internal constructor() : VGSDataAdapter() {

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private val listeners: MutableList<VGSNFCAdapterListener> = mutableListOf()

    abstract fun startReading()

    abstract fun stopReading()

    abstract fun handleNewIntent(intent: Intent?)

    fun addListener(listener: VGSNFCAdapterListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: VGSNFCAdapterListener) {
        listeners.remove(listener)
    }

    protected fun notifyReadingSuccess() {
        mainThreadHandler.post { listeners.forEach { it.onReadingSuccess() } }
    }

    protected fun notifyReadingFailed(reason: String) {
        mainThreadHandler.post { listeners.forEach { it.onReadingFailed(reason) } }
    }
}