package com.verygoodsecurity.vgscollect.view.card.validation.card.brand

import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator

/** @suppress */
class DinersClubDelegate : VGSValidator by DefaultCardValidator(CardType.DINCLUB.regex) {
    override fun isValid(content: String?): Boolean {
        return !content.isNullOrEmpty() && isLuhnCheckSumValid(content)
    }
}