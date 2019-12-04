package com.verygoodsecurity.vgscollect.view.card.discover

import android.text.InputFilter
import android.widget.TextView
import com.verygoodsecurity.vgscollect.view.card.CustomCardBrand
import com.verygoodsecurity.vgscollect.view.text.validation.card.CardType
import java.util.regex.Pattern

class CardBrandDetector(
    private val userCustomCardBrands: Array<CustomCardBrand>,
    private val inputField: TextView?
) : VGSCardDetector {

    override fun detect(str: String?): CardType {
        if(str.isNullOrEmpty()) {
            return CardType.NONE
        }

        val preparedStr = str.replace(" ", "")

        val values = CardType.values()
        for(i in values.indices) {
            val type = values[i]
            val m = Pattern.compile(type.regex).matcher(preparedStr)
            while (m.find()) {
                return tryToApply(type)
            }
        }

        for(i in userCustomCardBrands.indices) {
            val type = values[i]
            val m = Pattern.compile(type.regex).matcher(preparedStr)
            while (m.find()) {
                return tryToApply(type)
            }
        }

        inputField?.filters = arrayOf(InputFilter.LengthFilter(19 ))
        return CardType.NONE
    }

    private fun tryToApply(type: CardType): CardType {
        val length = type.rangeNumber.last()
        inputField?.filters = arrayOf(InputFilter.LengthFilter(length))
        return type
    }
}