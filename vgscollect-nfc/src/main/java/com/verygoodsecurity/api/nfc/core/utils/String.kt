package com.verygoodsecurity.api.nfc.core.utils

fun String.toHexByteArray(): ByteArray = chunked(2)
    .map { it.toInt(16).toByte() }
    .toByteArray()