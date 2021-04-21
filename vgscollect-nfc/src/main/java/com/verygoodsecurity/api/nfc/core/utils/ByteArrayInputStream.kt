package com.verygoodsecurity.api.nfc.core.utils

import android.util.Log
import com.verygoodsecurity.api.nfc.core.model.TLV
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.experimental.and

fun ByteArrayInputStream.getNextTLV(): TLV {
    // ISO/IEC 7816 uses neither '00' nor 'FF' as tag value.
    // Before, between, or after TLV-coded data objects,
    // '00' or 'FF' bytes without any meaning may occur
    // (for example, due to erased or modified TLV-coded data objects).

    available()

    mark(0)

    var peekInt = read()
    var peekByte = peekInt.toByte()

    // peekInt == 0xffffffff indicates EOS
    while (peekInt != -1 && (peekByte == 0xFF.toByte() || peekByte == 0x00.toByte())) {
        mark(0) // Current position
        peekInt = read()
        peekByte = peekInt.toByte()
    }
    reset() // Reset back to the last known position without 0x00 or 0xFF

    val tagIdBytes: ByteArray? = readTagIdBytes()

    // We need to get the raw length bytes.
    // Use quick and dirty workaround

    // We need to get the raw length bytes.
    // Use quick and dirty workaround
    mark(0)
    val posBefore = available()

    // Now parse the lengthbyte(s)
    // This method will read all length bytes. We can then find out how many bytes was read.
    // Now parse the lengthbyte(s)
    // This method will read all length bytes. We can then find out how many bytes was read.
    var length: Int = readTagLength() // Decoded

    // Now find the raw (encoded) length bytes
    // Now find the raw (encoded) length bytes
    val posAfter = available()
    reset()
    val lengthBytes = ByteArray(posBefore - posAfter).also {
        if (it.size < 1 || it.size > 4) {
            Log.e("test", "Number of length bytes must be from 1 to 4. Found " + it.size)
        }
    }

    read(lengthBytes, 0, lengthBytes.size)

    val rawLength: Int = lengthBytes.byteArrayToInt()

    val emv = tagIdBytes!!.getEMV()
// Find VALUE bytes

    val valueBytes: ByteArray
    if (rawLength == 128) { // 1000 0000
        mark(0)
        var prevOctet = 1
        var curOctet: Int
        var len = 0
        while (true) {
            len++
            curOctet = read()


            if (curOctet < 0) Log.e(
                "test",
                "Error parsing data. TLV " + "length byte indicated indefinite length, but EOS " + "was reached before 0x0000 was found" + available()
            )


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

        if (available() < length) Log.e(
            "test",
            "Length byte(s) indicated " + length + " value bytes, but only " + available() + " " + (if (available() > 1) "are" else "is") + " available"
        )

        // definite form
        valueBytes = ByteArray(length)
        read(valueBytes, 0, length)
    }

    // Remove any trailing 0x00 and 0xFF
    mark(0)
    peekInt = read()
    peekByte = peekInt.toByte()


    while (peekInt != -1 && (peekByte == 0xFF.toByte() || peekByte == 0x00.toByte())) {
        mark(0)
        peekInt = read()
        peekByte = peekInt.toByte()
    }
    reset() // Reset back to the last known position without 0x00 or 0xFF

    return TLV(emv, length, lengthBytes, valueBytes)
}


fun ByteArrayInputStream.readTagLength(): Int {
    // Find LENGTH bytes
    val length: Int
    var tmpLength: Int = read()

    if (tmpLength < 0) Log.e("test", "Negative length: $tmpLength")

    when {
        tmpLength <= 127 -> { // 0111 1111
            // short length form
            length = tmpLength
        }
        tmpLength == 128 -> { // 1000 0000
            // length identifies indefinite form, will be set later
            // indefinite form is not specified in ISO7816-4, but we include it here for completeness
            length = tmpLength
        }
        else -> {
            // long length form
            val numberOfLengthOctets = tmpLength and 127 // turn off 8th bit
            tmpLength = 0
            for (i in 0 until numberOfLengthOctets) {
                val nextLengthOctet: Int = read()

                if (nextLengthOctet < 0) Log.e("test", "EOS when reading length bytes")

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

    // Find TAG bytes
    val MASK = 0x1F.toByte()
    if (tagFirstOctet and MASK == MASK/*.toInt()*/) { // EMV book 3, Page 178 or Annex B1 (EMV4.3)
        // Tag field is longer than 1 byte
        do {
            val nextOctet = read()
            if (nextOctet < 0) {
                break
            }
            val tlvIdNextOctet = nextOctet
            tagBAOS.write(tlvIdNextOctet)

            if (tlvIdNextOctet.matchBitByBitIndex(7).not()
                || (tlvIdNextOctet.matchBitByBitIndex(7)
                && tlvIdNextOctet and 0x7f == 0)) {
                break
            }
        } while (true)
    }
    return tagBAOS.toByteArray()
}

fun Int.matchBitByBitIndex(pBitIndex: Int): Boolean {
    return if (pBitIndex in 0..31) {
        this and 1 shl pBitIndex != 0
    } else {
        Log.e("test", "parameter \'pBitIndex\' must be between 0 and 31. pBitIndex=$pBitIndex")
        false
    }
}


















