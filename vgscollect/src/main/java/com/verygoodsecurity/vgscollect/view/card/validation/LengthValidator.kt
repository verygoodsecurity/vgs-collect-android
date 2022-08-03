package com.verygoodsecurity.vgscollect.view.card.validation

internal data class LengthValidator constructor(
    internal val min: Int,
    internal val max: Int
) : VGSValidator {

    override fun isValid(content: String): Boolean {
        if (content.isEmpty()) return false
        return content.length in min..max
    }
}