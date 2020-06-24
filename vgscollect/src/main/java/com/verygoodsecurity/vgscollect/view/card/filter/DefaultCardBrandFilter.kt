package com.verygoodsecurity.vgscollect.view.card.filter

import androidx.annotation.VisibleForTesting
import com.verygoodsecurity.vgscollect.view.card.CardType
import java.util.regex.Pattern

/** @suppress */
class DefaultCardBrandFilter(
    private val cardBrands: Array<CardType>,
    private var divider:String? = " "
) : VGSCardFilter {

    override fun detect(str: String?): CardBrandPreview? {
        if(str.isNullOrEmpty()) {
            return CardBrandPreview()
        }
        val preparedStr = str.replace(divider?:" ", "")

        for(i in cardBrands.indices) {
            val value = cardBrands[i]
            val m = Pattern.compile(value.regex).matcher(preparedStr)
            while (m.find()) {
                return CardBrandPreview(value,
                            value.regex,
                            value.name,
                            value.resId,
                            value.mask,
                            value.algorithm,
                            value.rangeNumber,
                            value.rangeCVV,
                            value == CardType.NONE)
            }
        }

        return CardBrandPreview()
    }

    @VisibleForTesting
    fun setDivider(divider:String) {
        this.divider = divider
    }
}