package com.verygoodsecurity.vgscollect.view.card.validation

internal data class LengthValidator constructor(
    private val min: Int,
    private val max: Int
) : VGSValidator {

    override fun isValid(content: String?): Boolean {
        if (content.isNullOrEmpty()) return false
        return content.length in min..max
    }
}