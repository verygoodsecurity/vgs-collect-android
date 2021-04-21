package com.verygoodsecurity.api.nfc.core.utils

import com.verygoodsecurity.api.nfc.core.content.EMV
import com.verygoodsecurity.api.nfc.core.content.TrailerADPU
import com.verygoodsecurity.api.nfc.core.content.isConstructed
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

fun ByteArray.getTLVValue(vararg emv: EMV): ByteArray? {
    var ret: ByteArray? = null
    val stream = inputStream()
    while (stream.available() > 0) {
        val tlv: TLV = stream.getNextTLV()

        if (emv.contains(tlv.tag)) {
            ret = tlv.valueBytes
        } else if (tlv.tag.isConstructed()) {
            ret = tlv.valueBytes.getTLVValue(*emv)
        }
    }

    return ret
}


