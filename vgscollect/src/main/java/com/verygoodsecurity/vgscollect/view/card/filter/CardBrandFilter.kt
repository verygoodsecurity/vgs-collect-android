package com.verygoodsecurity.vgscollect.view.card.filter

import android.text.InputFilter
import android.util.Log
import android.widget.TextView
import com.verygoodsecurity.vgscollect.view.card.CustomCardBrand
import com.verygoodsecurity.vgscollect.view.text.validation.card.CardType
import java.util.regex.Pattern

class CardBrandFilter(
    private val userCustomCardBrands: Array<CustomCardBrand>,
    private val inputField: TextView?
) : VGSCardFilter {

    companion object {
        private const val MAX_LENGTH = 19
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
                inputField?.filters = arrayOf(InputFilter.LengthFilter(MAX_LENGTH))
                Log.e("test", "drawable: ${CardType.NONE.resId} ${value.drawableResId}")
                return CardBrandWrapper(regex = value.regex, name = value.cardBrandName, resId = value.drawableResId)
            }
        }

        inputField?.filters = arrayOf(InputFilter.LengthFilter(MAX_LENGTH))
        return null
    }
}