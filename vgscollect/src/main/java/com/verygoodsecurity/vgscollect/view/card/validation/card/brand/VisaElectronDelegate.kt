package com.verygoodsecurity.vgscollect.view.card.validation.card.brand

import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator

/** @suppress */
class VisaElectronDelegate : VGSValidator by DefaultCardValidator("^4(026|17500|405|508|844|91[37])") {
    override fun isValid(content: String?): Boolean {
        return !content.isNullOrEmpty() && isLuhnCheckSumValid(content)
    }
}