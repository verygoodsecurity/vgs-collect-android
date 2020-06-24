package com.verygoodsecurity.vgscollect.view.card.validation

class LengthValidator(
    private val length: Array<Int>
) : VGSValidator {
    override fun isValid(content: String?): Boolean {
        return !content.isNullOrEmpty() && length.contains(content.length)
    }
}