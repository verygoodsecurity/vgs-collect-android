package com.verygoodsecurity.vgscollect.app

interface VGSDataAdapter {

    fun addListener(listener: VGSDataAdapterListener)

    fun removeListener(listener: VGSDataAdapterListener)
}