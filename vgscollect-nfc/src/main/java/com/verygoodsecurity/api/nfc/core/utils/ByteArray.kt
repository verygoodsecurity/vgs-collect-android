package com.verygoodsecurity.api.nfc.core.utils

import com.verygoodsecurity.api.nfc.core.content.TrailerADPU
import java.util.*

fun ByteArray?.isSucceed(): Boolean {
    return this?.toTrailerADPU()?.run {
        this === TrailerADPU.SW_9000
    } ?: false
}

fun ByteArray.toTrailerADPU(): TrailerADPU? {
    return if (size >= 2) {
        TrailerADPU.values().toList().find {
            with(it.getStatus()) {
                val sw1 = this@toTrailerADPU[this@toTrailerADPU.size - 2]
                val sw2 = this@toTrailerADPU[this@toTrailerADPU.size - 1]
                size == 1 && sw1 == this[0]
                        || sw1 == this[0] && sw2 == this[1]
            }
        }
    } else null
}
