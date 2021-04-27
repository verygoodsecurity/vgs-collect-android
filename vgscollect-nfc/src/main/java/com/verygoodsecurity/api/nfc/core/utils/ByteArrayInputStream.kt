package com.verygoodsecurity.api.nfc.core.utils

import com.verygoodsecurity.api.nfc.core.model.TLV
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.experimental.and

fun ByteArrayInputStream.getNextTLV(): TLV {
    available()

    mark(0)

    var peekInt = read()
    var peekByte = peekInt.toByte()

    while (peekInt != -1 && (peekByte == 0xFF.toByte() || peekByte == 0x00.toByte())) {
        mark(0)
        peekInt = read()
        peekByte = peekInt.toByte()
    }
    reset()

    val tagIdBytes: ByteArray? = this.readTagIdBytes()

    mark(0)
    val posBefore = available()

    var length: Int = readTagLength()

    val posAfter = available()
    reset()
    val lengthBytes = ByteArray(posBefore - posAfter)

    read(lengthBytes, 0, lengthBytes.size)

    val rawLength: Int = lengthBytes.byteArrayToInt()

    val emv = tagIdBytes!!.getEMV()

    val valueBytes: ByteArray
    if (rawLength == 128) {
        mark(0)
        var prevOctet = 1
        var curOctet: Int
        var len = 0
        while (true) {
            len++
            curOctet = read()

            if (prevOctet == 0 && curOctet == 0) {
                break
            }
            prevOctet = curOctet
        }
        len -= 2
        valueBytes = ByteArray(len)
        reset()
        read(valueBytes, 0, len)
        length = len
    } else {
        valueBytes = ByteArray(length)
        read(valueBytes, 0, length)
    }

    mark(0)
    peekInt = read()
    peekByte = peekInt.toByte()


    while (peekInt != -1 && (peekByte == 0xFF.toByte() || peekByte == 0x00.toByte())) {
        mark(0)
        peekInt = read()
        peekByte = peekInt.toByte()
    }
    reset()

    return TLV(emv, length, lengthBytes, valueBytes)
}

fun ByteArrayInputStream.readTagLength(): Int {
    val length: Int
    var tmpLength: Int = read()

    when {
        tmpLength <= 128 -> {
            length = tmpLength
        }
        else -> {
            val numberOfLengthOctets = tmpLength and 127 // turn off 8th bit
            tmpLength = 0
            for (i in 0 until numberOfLengthOctets) {
                val nextLengthOctet: Int = read()

                tmpLength = tmpLength shl 8
                tmpLength = tmpLength or nextLengthOctet
            }
            length = tmpLength
        }
    }
    return length
}

fun ByteArrayInputStream.readTagIdBytes(): ByteArray? {
    val tagBAOS = ByteArrayOutputStream()
    val tagFirstOctet = read().toByte()
    tagBAOS.write(tagFirstOctet.toInt())

    val MASK = 0x1F.toByte()
    if (tagFirstOctet and MASK == MASK) {
        do {
            val nextOctet = read()
            if (nextOctet < 0) {
                break
            }
            tagBAOS.write(nextOctet)

            if (nextOctet.matchBitByBitIndex(7).not()
                || (nextOctet.matchBitByBitIndex(7)
                        && nextOctet and 0x7f == 0)
            ) {
                break
            }
        } while (true)
    }
    return tagBAOS.toByteArray()
}
