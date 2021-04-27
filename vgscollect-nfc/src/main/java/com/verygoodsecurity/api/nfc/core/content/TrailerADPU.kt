package com.verygoodsecurity.api.nfc.core.content

import com.verygoodsecurity.api.nfc.core.utils.toHexByteArray

enum class TrailerADPU(
    private val statusBytes: String
) {

    SW_6500("6500"),
    SW_6C("6C"),

    /** Command successfully executed (OK) */
    SW_9000("9000");

    fun getStatus(): ByteArray {
        return statusBytes.toHexByteArray()
    }
}