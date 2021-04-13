package com.verygoodsecurity.api.nfc

abstract class VGSNFCAdapterListener {

    open fun onReadingSuccess() {}

    open fun onReadingFailed(reason: String? = null) {}
}