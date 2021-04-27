package com.verygoodsecurity.api.nfc.core.utils

import com.verygoodsecurity.api.nfc.core.content.EMV


fun EMV.isConstructed(): Boolean {
    return id.toHexByteArray()[0].toInt().matchBitByBitIndex(5)
}