package com.verygoodsecurity.api.nfc.core.model

import com.verygoodsecurity.api.nfc.core.content.EMV


class TLV(
    val tag: EMV,
    val length: Int,
    val rawEncodedLengthBytes: ByteArray,
    val valueBytes: ByteArray,
) {
    fun getBytes(): ByteArray {
        return valueBytes
    }
}

