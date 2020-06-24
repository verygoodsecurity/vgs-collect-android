package com.verygoodsecurity.vgscollect.view.card.filter

import androidx.annotation.VisibleForTesting
import com.verygoodsecurity.vgscollect.view.card.CustomCardBrand
import java.util.regex.Pattern

/** @suppress */
class CardBrandFilter(
    private var divider:String? = ""
) : MutableCardFilter {

    private val userCustomCardBrands = mutableListOf<CustomCardBrand>()


    override fun add(item: CustomCardBrand?) {
        item?.let {
            userCustomCardBrands.add(item)
        }
    }

    override fun detect(str: String?): CardBrandPreview? {
        if(str.isNullOrEmpty()) {
            return CardBrandPreview()
        }
        val preparedStr = str.replace(divider?:" ", "")

        for(i in userCustomCardBrands.indices) {
            val value = userCustomCardBrands[i]
            val m = Pattern.compile(value.regex).matcher(preparedStr)
            while (m.find()) {
                return CardBrandPreview(
                    regex = value.regex,
                    name = value.cardBrandName,
                    resId = value.drawableResId,
                    currentMask = value.params.mask,
                    algorithm = value.params.algorithm,
                    numberLength = value.params.rangeNumber,
                    cvcLength =  value.params.rangeCVV)
            }
        }

        return null
    }

    @VisibleForTesting
    fun setDivider(divider:String) {
        this.divider = divider
    }
}