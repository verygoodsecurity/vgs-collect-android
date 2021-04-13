package com.verygoodsecurity.api.nfc

import android.content.Intent
import com.verygoodsecurity.vgscollect.app.VGSDataAdapter
import com.verygoodsecurity.vgscollect.app.VGSDataAdapterListener
import com.verygoodsecurity.vgscollect.core.model.VGSHashMapWrapper

abstract class NFCAdapter internal constructor() : VGSDataAdapter {

    private val listeners: MutableList<VGSDataAdapterListener> = mutableListOf()

    abstract fun startReading()

    abstract fun stopReading()

    abstract fun handleNewIntent(intent: Intent?)

    override fun addListener(listener: VGSDataAdapterListener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: VGSDataAdapterListener) {
        listeners.remove(listener)
    }

    protected fun notifyDataReceived(data: VGSHashMapWrapper<String, Any?>) {
        listeners.forEach { it.onDataReceived(data) }
    }

    protected fun notifyDataReceiveFailed(reason: String) {
        listeners.forEach { it.onDataReceiveFailed(reason) }
    }
}