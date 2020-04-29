package com.verygoodsecurity.vgscollect.view.card.validation.card.brand

import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator

/** @suppress */
class DiscoverDelegate : VGSValidator by DefaultCardValidator("^6(?:011|5[0-9]{2})[0-9]{12}\$") {
    override fun isValid(content: String?): Boolean {
        return !content.isNullOrEmpty() && isLuhnChecksumValid(content)
    }
}