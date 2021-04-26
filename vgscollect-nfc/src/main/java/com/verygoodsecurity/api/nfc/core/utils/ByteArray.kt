package com.verygoodsecurity.api.nfc.core.utils

import com.verygoodsecurity.api.nfc.core.content.EMV
import com.verygoodsecurity.api.nfc.core.content.TrailerADPU
import com.verygoodsecurity.api.nfc.core.content.isConstructed
import com.verygoodsecurity.api.nfc.core.model.ApplicationFileLocator
import com.verygoodsecurity.api.nfc.core.model.TLV
import java.util.*

fun ByteArray?.isSuccessful(): Boolean {
    return this?.toTrailerADPU()?.run {
        this === TrailerADPU.SW_9000
    } ?: false
}

fun ByteArray?.isNotSuccessful(): Boolean = !this.isSuccessful()

fun ByteArray?.compareADPU(trailer: TrailerADPU): Boolean {
    return this?.toTrailerADPU()?.run {
        this === trailer
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

fun ByteArray.byteArrayToInt(): Int {
    return if (size in 1..4) {
        var value = 0
        for (i in indices) {
            value += get(i).toInt() and 255 shl 8 * (size - i - 1)
        }
        value
    } else {
        -1
    }
}

fun ByteArray.getEMV(): EMV {
    return EMV.values().find {
        val idBytes = it.id.toHexByteArray()
        idBytes.contentEquals(this)
    } ?: EMV.UNKNOWN
}

fun ByteArray.getAids(): List<ByteArray> {
    return with(mutableListOf<ByteArray>()) {
        getListTLV(EMV.AID_CARD, EMV.KERNEL_IDENTIFIER).forEach {
            if (it.tag === EMV.KERNEL_IDENTIFIER && this@with.size != 0) {
                when {
                    this@with.isEmpty() -> this@with.add(it.valueBytes)
                    it.valueBytes.isEmpty() -> {
                    }
                    else -> {
                        val joinedArray = ByteArray(this@with.size + it.valueBytes.size)
                        System.arraycopy(
                            this@with, 0, joinedArray, 0, this@with.size
                        )
                        System.arraycopy(
                            it.valueBytes, 0, joinedArray, this@with.size, it.valueBytes.size
                        )
                        this@with.add(joinedArray)
                    }
                }
            } else {
                this@with.add(it.valueBytes)
            }
        }
        this
    }
}

fun ByteArray.getListTLV(vararg emv: EMV): MutableList<TLV> {
    val list = mutableListOf<TLV>()
    val stream = inputStream()

    while (stream.available() > 0) {
        val tlv = stream.getNextTLV()

        if (emv.contains(tlv.tag)) {
            list.add(tlv)
        } else if (tlv.tag.isConstructed()) {
            val l = tlv.valueBytes.getListTLV(*emv)
            list.addAll(l)
        }
    }

    return list
}

fun ByteArray?.getTLVValue(vararg emv: EMV): ByteArray? {
    var ret: ByteArray? = null
    if(this != null) {
        val stream = inputStream()
        while (stream.available() > 0) {
            val tlv: TLV = stream.getNextTLV()

            if (emv.contains(tlv.tag)) {
                ret = tlv.valueBytes
            } else if (tlv.tag.isConstructed()) {
                val sentBytes = tlv.getBytes()
                ret = tlv.valueBytes.getTLVValue(*emv)
            } else {
            }
        }
    }

    return ret
}

fun ByteArray.parseTotalTagsLength(): Int {
    val tagAndLengthList = mutableListOf<Pair<EMV, Int>>()

    val stream = inputStream()
    while (stream.available() > 0) {
        val tagIdBytes: ByteArray? = ByteUtil.readTagIdBytes(stream)
        val tag: EMV? = tagIdBytes?.getEMV()
        val tagValueLength: Int = stream.readTagLength()

        if(tag != null) tagAndLengthList.add(tag to tagValueLength)
    }
    return tagAndLengthList.getLength()
}


fun MutableList<Pair<EMV, Int>>.getLength(): Int {
    var ret = 0
    forEach {
        ret += it.second
    }
    return ret
}












fun ByteArray.extractApplicationFileLocator():MutableList<ApplicationFileLocator> {
    val list: MutableList<ApplicationFileLocator> = ArrayList()

    val stream = inputStream()
    while (stream.available() >= 4) {
        val fl = ApplicationFileLocator(
            stream.read() shr 3,
            stream.read(),
            stream.read(),
            stream.read() == 1
        )

        list.add(fl)
    }
    return list
}

fun ByteArray.bytesToStringNoSpace(): String {
    val sb = StringBuffer()
    var t = false
    val `arr$`: ByteArray = this
    val `len$`: Int = this.size
    for (`i$` in 0 until `len$`) {
        val b = `arr$`[`i$`]
        if (b.toInt() != 0 || t) {
            t = true
            sb.append(String.format("%02x", *arrayOf<Any>(Integer.valueOf(b.toInt() and 255))))
        }
    }

    return sb.toString().toUpperCase(Locale.getDefault()).trim { it <= ' ' }
}




