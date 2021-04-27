package com.verygoodsecurity.api.nfc.core.utils

import com.verygoodsecurity.api.nfc.core.content.EMV

fun EMV.isConstructed(): Boolean = id.toHexByteArray()
    .first().toInt()
    .matchBitByBitIndex(5)