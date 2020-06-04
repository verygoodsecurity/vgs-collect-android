package com.verygoodsecurity.vgscollect.view.card.filter

import android.text.InputFilter
import android.widget.TextView
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.view.card.CustomCardBrand
import java.util.regex.Pattern

/** @suppress */
class CardBrandFilter(
    private val divider:String? = ""
) : MutableCardFilter {

    private val userCustomCardBrands = mutableListOf<CustomCardBrand>()


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
                return CardBrandWrapper(regex = value.regex, name = value.cardBrandName, resId = value.drawableResId)
            }
        }

        return null
    }
}