package com.verygoodsecurity.api.nfc.core.utils

import com.verygoodsecurity.api.nfc.core.content.EMV
import com.verygoodsecurity.api.nfc.core.content.TrailerADPU
import com.verygoodsecurity.api.nfc.core.content.isConstructed
import com.verygoodsecurity.api.nfc.core.model.TLV
import java.util.*

fun ByteArray?.isSucceed(): Boolean {
    return this?.toTrailerADPU()?.run {
        this === TrailerADPU.SW_9000
    } ?: false
}

fun ByteArray?.compareADPU(adpu: TrailerADPU): Boolean {
    return this?.toTrailerADPU()?.run {
        this === adpu
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
    var testEMV:EMV = EMV.UNKNOWN
    EMV.values().forEach {
        val a = it.id.toHexByteArray()
        if(a.contentEquals(this)) {
            testEMV =  it
        }
    }

    return testEMV
}

fun ByteArray.getTLVValue(vararg emv: EMV): ByteArray? {
    var ret: ByteArray? = null
    val stream = inputStream()
    while (stream.available() > 0) {
        val tlv: TLV = stream.getNextTLV()

        if (emv.contains(tlv.tag)) {
            ret = tlv.valueBytes
        } else if(tlv.tag.isConstructed()) {
            ret = tlv.valueBytes.getTLVValue(*emv)
        }
    }

    return ret
}

fun ByteArray.getAids(): List<ByteArray> {
    val ret: MutableList<ByteArray> = ArrayList()
    val listTlv: MutableList<TLV> = getListTLV(EMV.AID_CARD, EMV.KERNEL_IDENTIFIER)

    listTlv.forEach {
        if (it.tag === EMV.KERNEL_IDENTIFIER && ret.size != 0) {
            when {
                ret.isEmpty() -> ret.add(it.valueBytes)
                it.valueBytes.isEmpty() -> { }
                else -> {
                    val joinedArray = ByteArray(ret.size + it.valueBytes.size)
                    System.arraycopy(ret, 0, joinedArray, 0, ret.size)
                    System.arraycopy(it.valueBytes, 0, joinedArray, ret.size, it.valueBytes.size)
                    ret.add(joinedArray)
                }
            }
        } else {
            ret.add(it.valueBytes)
        }
    }

    return ret
}

fun ByteArray.getListTLV(vararg emv: EMV): MutableList<TLV> {
    val list = mutableListOf<TLV>()
    val stream = inputStream()

    while (stream.available() > 0) {
        val tlv = stream.getNextTLV()

        if (emv.contains(tlv.tag)) {
            list.add(tlv)
        } else if(tlv.tag.isConstructed()) {
            val l = tlv.valueBytes.getListTLV(*emv)
            list.addAll(l)
        }
    }

    return list
}

