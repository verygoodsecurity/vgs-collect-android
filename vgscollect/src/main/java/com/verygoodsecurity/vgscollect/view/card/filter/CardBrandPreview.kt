package com.verygoodsecurity.vgscollect.view.card.filter

import com.verygoodsecurity.vgscollect.view.card.CardType

/** @suppress */
data class CardBrandPreview(
    val cardType: CardType = CardType.NONE,
    val regex:String = CardType.NONE.regex,
    val name:String? = CardType.NONE.name,
    val resId:Int = CardType.NONE.resId,
    var currentMask:String = CardType.NONE.mask,
    var hasLuhn:Boolean = false
)