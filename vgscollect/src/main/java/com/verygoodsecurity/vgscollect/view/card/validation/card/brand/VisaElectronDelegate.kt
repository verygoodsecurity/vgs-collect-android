package com.verygoodsecurity.vgscollect.view.card.validation.card.brand

import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator

class VisaElectronDelegate : VGSValidator by DefaultCardValidator() {
    override fun isValid(content: String?): Boolean {
        return !content.isNullOrEmpty() && isLuhnCheckSumValid(content)
    }
}