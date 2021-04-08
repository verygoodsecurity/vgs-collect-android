package com.verygoodsecurity.vgscollect.view.card.filter

import com.verygoodsecurity.vgscollect.view.card.CardBrand

/** @suppress */
interface MutableCardFilter : VGSCardFilter {

    fun addCustomCardBrand(brand: CardBrand)

    fun setValidCardBrands(cardBrands: List<CardBrand>)
}