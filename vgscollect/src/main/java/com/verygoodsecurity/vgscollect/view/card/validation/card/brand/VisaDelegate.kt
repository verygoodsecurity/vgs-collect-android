package com.verygoodsecurity.vgscollect.view.card.validation.card.brand

import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator

/** @suppress */
class VisaDelegate : VGSValidator by DefaultCardValidator("^4[0-9]{12}((?:[0-9]{3})?|(?:[0-9]{6})?)\$") {
    override fun isValid(content: String?): Boolean {
        return !content.isNullOrEmpty() && isLuhnCheckSumValid(content)
    }
}