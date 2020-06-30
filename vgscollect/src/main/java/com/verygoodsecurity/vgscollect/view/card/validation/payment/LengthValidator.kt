package com.verygoodsecurity.vgscollect.view.card.validation.payment

import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator

class LengthValidator(
    private val length: Array<Int>
) : VGSValidator {
    override fun isValid(content: String?): Boolean {
        return !content.isNullOrEmpty() && length.contains(content.length)
    }
}