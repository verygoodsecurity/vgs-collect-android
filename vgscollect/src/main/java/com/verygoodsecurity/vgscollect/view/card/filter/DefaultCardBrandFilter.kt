package com.verygoodsecurity.vgscollect.view.card.filter

import android.text.InputFilter
import android.widget.TextView
import com.verygoodsecurity.vgscollect.view.card.CardType
import java.util.regex.Pattern

class DefaultCardBrandFilter(
    private val cardBrands: Array<CardType>,
    private val inputField: TextView?,
    private val divider:String? = " "
) : VGSCardFilter {

    companion object {
        private const val MAX_LENGTH = 19
        private const val MASK_COUNT = 3
    }

    override fun detect(str: String?): CardBrandWrapper? {
        if(str.isNullOrEmpty()) {
            return CardBrandWrapper()
        }
        val preparedStr = str.replace(divider?:" ", "")

        for(i in cardBrands.indices) {
            val value = cardBrands[i]
            val m = Pattern.compile(value.regex).matcher(preparedStr)
            while (m.find()) {
                val length = value.rangeNumber.last()
                inputField?.filters = arrayOf(InputFilter.LengthFilter(length + MASK_COUNT))
                return CardBrandWrapper(value, value.regex, value.name, value.resId)
            }
        }

        inputField?.filters = arrayOf(InputFilter.LengthFilter(MAX_LENGTH + MASK_COUNT))
        return CardBrandWrapper()
    }
}