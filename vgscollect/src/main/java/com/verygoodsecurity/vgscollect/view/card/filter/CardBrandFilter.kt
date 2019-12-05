package com.verygoodsecurity.vgscollect.view.card.filter

import android.text.InputFilter
import android.widget.TextView
import com.verygoodsecurity.vgscollect.view.card.CustomCardBrand
import java.util.regex.Pattern

class CardBrandFilter(
    private val userCustomCardBrands: Array<CustomCardBrand>,
    private val inputField: TextView?
) : VGSCardFilter {

    companion object {
        private const val MAX_LENGTH = 19
        private const val MASK_COUNT = 3
    }

    override fun detect(str: String?): CardBrandWrapper? {
        if(str.isNullOrEmpty()) {
            return CardBrandWrapper()
        }
        val preparedStr = str.replace(" ", "")

        for(i in userCustomCardBrands.indices) {
            val value = userCustomCardBrands[i]
            val m = Pattern.compile(value.regex).matcher(preparedStr)
            while (m.find()) {
                inputField?.filters = arrayOf(InputFilter.LengthFilter(MAX_LENGTH + MASK_COUNT))
                return CardBrandWrapper(regex = value.regex, name = value.cardBrandName, resId = value.drawableResId)
            }
        }

        inputField?.filters = arrayOf(InputFilter.LengthFilter(MAX_LENGTH + MASK_COUNT))
        return null
    }
}