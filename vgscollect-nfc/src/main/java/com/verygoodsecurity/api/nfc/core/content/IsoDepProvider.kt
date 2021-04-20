package com.verygoodsecurity.api.nfc.core.content

import android.nfc.Tag
import android.nfc.tech.IsoDep
import java.io.IOException

internal class IsoDepProvider(tag: Tag) {

    private val isoDep = IsoDep.get(tag)

    fun connect() {
        isoDep.connect()
    }

    fun transceive(command: ByteArray): ByteArray? {
        return try {
            isoDep?.transceive(command)
        } catch (e: IOException) {
            null
        }
    }

}