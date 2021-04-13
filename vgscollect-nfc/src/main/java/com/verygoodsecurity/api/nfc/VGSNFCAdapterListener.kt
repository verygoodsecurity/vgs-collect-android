package com.verygoodsecurity.api.nfc

interface VGSNFCAdapterListener {

    fun onReadingSuccess()

    fun onReadingFailed(reason: String? = null)
}