package com.verygoodsecurity.vgscollect.view.card.validation

/** @suppress */
internal class LengthMatchValidator : VGSValidator {

    internal val values: Array<Int>

    override val errorMsg: String

    constructor(length: Int, errorMsg: String = DEFAULT_ERROR_MSG) {
        this.values = arrayOf(length)
        this.errorMsg = errorMsg
    }

    constructor(length: Array<Int>, errorMsg: String = DEFAULT_ERROR_MSG) {
        this.values = length
        this.errorMsg = errorMsg
    }

    override fun isValid(content: String) = values.contains(content.length)

    internal companion object {

        internal const val DEFAULT_ERROR_MSG = "LENGTH_RANGE_MATCH_VALIDATION_ERROR"
    }
}