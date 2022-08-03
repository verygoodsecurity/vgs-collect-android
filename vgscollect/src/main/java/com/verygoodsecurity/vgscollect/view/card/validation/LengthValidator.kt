package com.verygoodsecurity.vgscollect.view.card.validation

internal data class LengthValidator constructor(
    internal val min: Int,
    internal val max: Int,
    override val errorMsg: String = DEFAULT_ERROR_MSG,
) : VGSValidator {

    override fun isValid(content: String) = content.length in min..max

    internal companion object {

        internal const val DEFAULT_ERROR_MSG = "LENGTH_VALIDATION_ERROR"
    }
}