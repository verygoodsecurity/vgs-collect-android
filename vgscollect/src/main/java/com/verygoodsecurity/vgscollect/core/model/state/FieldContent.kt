package com.verygoodsecurity.vgscollect.core.model.state

import com.verygoodsecurity.vgscollect.util.isNumeric
import com.verygoodsecurity.vgscollect.view.card.CardType
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

/** @suppress */
sealed class FieldContent {
    var data:String? = null
        internal set

    class CardNumberContent:FieldContent() {
        var rawData:String? = null
        var cardtype: CardType = CardType.UNKNOWN
        var numberRange: Array<Int> = CardType.UNKNOWN.rangeNumber
        var rangeCVV: Array<Int> = CardType.UNKNOWN.rangeCVV
        var iconResId:Int? = 0
            internal set
        var cardBrandName:String? = null
            internal set
    }

    class CreditCardExpDateContent:FieldContent() {
        var rawData:String? = null
    }

    class InfoContent:FieldContent()

    override fun hashCode(): Int {
        return data.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is String && other == data
    }

    override fun toString(): String {
        return data?:""
    }
}

/** @suppress */
internal fun FieldContent.CreditCardExpDateContent.handleOutputFormat(selectedDate: Calendar, fieldDateFormat: SimpleDateFormat?, fieldDateOutPutFormat: SimpleDateFormat?) {
    if(fieldDateFormat != null && fieldDateFormat.toPattern() == fieldDateOutPutFormat?.toPattern()) {
        data = fieldDateFormat.format(selectedDate.time)
        rawData = data
    } else {
        data = fieldDateFormat?.format(selectedDate.time)
        rawData = fieldDateOutPutFormat?.format(selectedDate.time)
    }
}

/** @suppress */
internal fun FieldContent.CardNumberContent.parseCardBin():String {
    return data?.run {
        val numberSTR = data!!.replace("\\D".toRegex(), "")
        if(numberSTR.length >= 6) {
            numberSTR.substring(0, 6)
        } else {
            numberSTR.substring(0, numberSTR.length)
        }
    }?:""
}

/** @suppress */
internal fun FieldContent.CardNumberContent.parseCardLast4Digits():String {
    return data?.run {
        val numberSTR = data!!.replace("\\D".toRegex(), "")
        if(numberSTR.length > 10) {
            val start = numberSTR.length - 4
            val end = numberSTR.length
            numberSTR.substring(start, end)
        } else {
            ""
        }
    }?:""
}

/** @suppress */
internal fun FieldContent.CardNumberContent.parseRawCardBin():String {
    return data?.run {
        val numberSTR = this.replace("\\D".toRegex(), "")
        val binEnd = this.cardNumberBinEnd()
        if(numberSTR.length >= binEnd) {
            substring(0, binEnd)
        } else {
            substring(0, numberSTR.length)
        }
    }?:""
}

/** @suppress */
internal fun FieldContent.CardNumberContent.parseRawCardLastDigits():String {
    return data?.run {
        val maxCount = this.cardNumberLastDigStart()
        if(length > maxCount) {
            substring(maxCount, length)
        } else {
            ""
        }
    }?:""
}

/** @suppress */
internal fun FieldContent.CardNumberContent.parseCardNumber():String? {
    if(this.data.isNullOrEmpty()) {
        return ""
    }
    val startDig = this.data!!.cardNumberBinEnd()
    val endDig = this.data!!.cardNumberLastDigStart()

    val str = if(data!!.length <= startDig) {
        data
    } else if (data!!.length < endDig) {
        val bin = parseRawCardBin()
        val dif = data!!.length - bin.length
        if(dif > 0) {
            val start = bin.length
            val end = data!!.length
            val mask = data!!.substring(start, end).replace("\\d".toRegex(), "#")

            bin + mask
        } else {
            bin
        }
    } else {
        val builder = StringBuilder()
        val bin = parseRawCardBin()
        val last = parseRawCardLastDigits()

        val start = bin.length
        val end = data!!.length - last.length
        val mask = data!!.substring(start, end).replace("\\d".toRegex(), "#")

        builder.append(bin)
            .append(mask)
            .append(last)
            .toString()
    }
    return str

}

private fun String.cardNumberBinEnd():Int {
    return if (this.isNumeric()) { 6 } else { 7 }
}

private fun String.cardNumberLastDigStart():Int {
    return if (this.isNumeric()) {
        12
    } else {
        15
    }
}
