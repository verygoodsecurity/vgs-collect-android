package com.verygoodsecurity.vgscollect.core.model.state

import com.verygoodsecurity.vgscollect.util.isNumeric
import com.verygoodsecurity.vgscollect.view.card.CardType
import java.lang.StringBuilder

sealed class FieldContent {
    var data:String? = null
        internal set

    class CardNumberContent:FieldContent() {
        var cardtype: CardType = CardType.NONE
        var iconResId:Int? = 0
            internal set
        var cardBrandName:String? = null
            internal set
    }

    class InfoContent:FieldContent()
}

internal fun FieldContent.CardNumberContent.CVCMaxLength() =  cardtype.rangeCVV.last()

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

internal fun FieldContent.CardNumberContent.parseRawCardBin():String {
    return data?.run {
        val numberSTR = data!!.replace("\\D".toRegex(), "")
        if(numberSTR.length >= 7) {
            substring(0, 7)
        } else {
            substring(0, numberSTR.length)
        }
    }?:""
}

internal fun FieldContent.CardNumberContent.parseRawCardLastDigits():String {
    return data?.run {
        val maxCount = if (data!!.isNumeric()) { 12 } else { 15 }
        if(length > maxCount) {
            substring(maxCount, length)
        } else {
            ""
        }
    }?:""
}

internal fun FieldContent.CardNumberContent.parseCardNumber():String? {
    val startDig:Int
    val endDig:Int
    when {
        this.data.isNullOrEmpty() -> {
            return ""
        }
        data!!.isNumeric() -> {
            startDig = 6
            endDig = 12
        }
        else -> {
            startDig = 7
            endDig = 15
        }
    }
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