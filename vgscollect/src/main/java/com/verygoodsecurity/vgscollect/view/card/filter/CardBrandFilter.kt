package com.verygoodsecurity.vgscollect.view.card.filter

import androidx.annotation.VisibleForTesting
import com.verygoodsecurity.vgscollect.util.extension.except
import com.verygoodsecurity.vgscollect.util.extension.toCardBrands
import com.verygoodsecurity.vgscollect.view.card.CardBrand
import com.verygoodsecurity.vgscollect.view.card.CardType
import java.util.regex.Pattern

/** @suppress */
class CardBrandFilter(private var divider: String? = DEFAULT_DIVIDER) : MutableCardFilter {

    private val customCardBrands = mutableListOf<CardBrand>()
    private val availableCardBrands: List<CardBrand> get() = customCardBrands + DEFAULT_BRANDS
    private var validCardBrands: List<CardBrand>? = null

    override fun addCustomCardBrand(brand: CardBrand) {
        customCardBrands.add(brand)
    }

    override fun setValidCardBrands(cardBrands: List<CardBrand>) {
        this.validCardBrands = cardBrands
    }

    override fun detect(data: String?): CardBrandPreview {
        if (data.isNullOrEmpty()) return CardBrandPreview()
        val withoutDividerData = data.replace(divider ?: DEFAULT_DIVIDER, "")
        (validCardBrands ?: availableCardBrands).forEach {
            val matcher = Pattern.compile(it.regex).matcher(withoutDividerData)
            while (matcher.find()) {
                return CardBrandPreview(
                    cardType = it.cardType,
                    regex = it.regex,
                    name = it.cardBrandName,
                    resId = it.drawableResId,
                    currentMask = it.params.mask,
                    algorithm = it.params.algorithm,
                    numberLength = it.params.rangeNumber,
                    cvcLength = it.params.rangeCVV,
                    successfullyDetected = true
                )
            }
        }
        return CardBrandPreview()
    }

    @VisibleForTesting
    fun setDivider(divider: String) {
        this.divider = divider
    }

    companion object {

        private const val DEFAULT_DIVIDER = " "
        private val DEFAULT_BRANDS = CardType.values().except(CardType.UNKNOWN).toCardBrands()
    }
}