package com.verygoodsecurity.vgscollect.view.card.filter

import android.text.InputFilter
import android.widget.TextView
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.view.card.CustomCardBrand
import java.util.regex.Pattern

class CardBrandFilter(
    private val inputField: TextView? = null,
    private val divider:String? = ""
) : MutableCardFilter {

    private val userCustomCardBrands = mutableListOf<CustomCardBrand>()

    companion object {
        private const val MAX_LENGTH = 19
        private const val MASK_COUNT = 3
    }

    override fun add(item: CustomCardBrand?) {
        item?.let {
            userCustomCardBrands.add(item)
        }
    }

    override fun detect(str: String?): CardBrandWrapper? {
        if(str.isNullOrEmpty()) {
            return CardBrandWrapper()
        }
        val preparedStr = str.replace(divider?:" ", "")

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