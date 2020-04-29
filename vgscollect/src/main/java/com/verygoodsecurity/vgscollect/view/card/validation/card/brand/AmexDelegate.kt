package com.verygoodsecurity.vgscollect.view.card.validation.card.brand

import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator

/** @suppress */
class AmexDelegate : VGSValidator by DefaultCardValidator("^3[47][0-9]{13}\$") {
    override fun isValid(content: String?): Boolean {
        return !content.isNullOrEmpty() && isLuhnChecksumValid(content)
    }
}
