package com.verygoodsecurity.vgscollect.view.card.filter

import android.text.InputFilter
import android.widget.TextView
import com.verygoodsecurity.vgscollect.view.card.CardType
import java.util.regex.Pattern

/** @suppress */
class DefaultCardBrandFilter(
    private val cardBrands: Array<CardType>,
    private val divider:String? = " "
) : VGSCardFilter {

    override fun detect(str: String?): CardBrandWrapper? {
        if(str.isNullOrEmpty()) {
            return CardBrandWrapper()
        }
        val preparedStr = str.replace(divider?:" ", "")

        for(i in cardBrands.indices) {
            val value = cardBrands[i]
            val m = Pattern.compile(value.regex).matcher(preparedStr)
            while (m.find()) {
                return CardBrandWrapper(value, value.regex, value.name, value.resId)
            }
        }

        return CardBrandWrapper()
    }
}