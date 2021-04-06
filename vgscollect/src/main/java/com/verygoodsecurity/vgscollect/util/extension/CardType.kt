package com.verygoodsecurity.vgscollect.util.extension

import com.verygoodsecurity.vgscollect.view.card.BrandParams
import com.verygoodsecurity.vgscollect.view.card.CardBrand
import com.verygoodsecurity.vgscollect.view.card.CardType

/**
 * Map CardType to CardBrand.
 *
 * @return newly created CardBrand
 */
fun CardType.toCardBrand(): CardBrand {
    return CardBrand(
        this,
        this.regex,
        this.name,
        this.resId,
        BrandParams(
            this.mask,
            this.algorithm,
            this.rangeNumber,
            this.rangeCVV
        )
    )
}

/**
 * Map array CardTypes to list of CardBrands.
 *
 * @return list of newly created CardBrands.
 */
fun Array<CardType>.toCardBrands(): List<CardBrand> {
    return this.map { it.toCardBrand() }
}