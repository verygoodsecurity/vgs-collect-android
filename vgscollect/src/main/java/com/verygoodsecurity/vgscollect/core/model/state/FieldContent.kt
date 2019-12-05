package com.verygoodsecurity.vgscollect.core.model.state

import com.verygoodsecurity.vgscollect.view.text.validation.card.CardType
import java.lang.StringBuilder

sealed class FieldContent {
    var data:String? = null
        internal set

    object CardNumberContent:FieldContent() {
        var cardtype: CardType = CardType.NONE
        var iconResId:Int? = 0
            internal set
        var cardBrandName:String? = null
            internal set
    }

    class InfoContent:FieldContent()
}

internal fun FieldContent.CardNumberContent.parseCardBin():String? {
    return data!!.run {
        if(length >= 7) {
            substring(0, 7)
        } else {
            substring(0, length)
        }
    }
}

internal fun FieldContent.CardNumberContent.parseCardLast4():String? {
    return data!!.replace(" ", "").run {
        val minCardCount = 12
        if(length > minCardCount) {
            substring(minCardCount, length)
        } else {
            ""
        }
    }
}

internal fun FieldContent.CardNumberContent.parseCardNumber():String? {
    return if(data != null) {
        val str = if(data!!.length <= 7) {
            data
        } else if (data!!.length < 15) {
            val bin = data!!.run {
                if(length >= 7) {
                    substring(0, 7)
                } else {
                    substring(0, length)
                }
            }
            val dif = data!!.length - bin.length
            if(dif > 0) {
                val mask = "#".repeat(dif)
                bin + mask
            } else {
                bin
            }
        } else {
            val builder = StringBuilder()
            val bin = data!!.run {
                if(length >= 7) {
                    substring(0, 7)
                } else {
                    substring(0, length)
                }
            }
            val last4 = data!!.run {
                if(length > 14) {
                    substring(14, length)
                } else {
                    ""
                }
            }
            val mask = "#".repeat(7)

            builder.append(bin)
                .append(mask)
                .append(last4)
                .toString()
        }
        str
    } else {
        ""
    }

}