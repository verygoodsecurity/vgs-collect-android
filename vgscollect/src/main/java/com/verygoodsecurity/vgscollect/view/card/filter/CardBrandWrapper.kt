package com.verygoodsecurity.vgscollect.view.card.filter

import com.verygoodsecurity.vgscollect.view.text.validation.card.CardType

data class CardBrandWrapper(
    val cardType:CardType = CardType.NONE,
    val regex:String = CardType.NONE.regex,
    val name:String? = CardType.NONE.name,
    val resId:Int = CardType.NONE.resId
)