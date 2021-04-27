package com.verygoodsecurity.api.nfc.core.utils


fun Int.matchBitByBitIndex(pBitIndex: Int): Boolean {
    return if (pBitIndex in 0..31) {
        (this and (1 shl pBitIndex)) != 0
    } else {
        false
    }
}