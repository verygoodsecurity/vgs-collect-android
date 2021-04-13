package com.verygoodsecurity.vgscollect.app

import com.verygoodsecurity.vgscollect.core.model.VGSHashMapWrapper

interface VGSDataAdapterListener {

    fun onDataReceived(data: VGSHashMapWrapper<String, Any?>)

    fun onDataReceiveFailed(reason: String? = null)
}