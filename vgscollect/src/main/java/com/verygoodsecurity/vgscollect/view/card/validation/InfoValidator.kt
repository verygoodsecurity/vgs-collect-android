package com.verygoodsecurity.vgscollect.view.card.validation

/** @suppress */
class InfoValidator : VGSValidator {

    override val errorMsg: String = DEFAULT_ERROR_MSG

    override fun isValid(content: String) = content.trim().isNotEmpty()

    internal companion object {

        internal const val DEFAULT_ERROR_MSG = "INFO_FIELD_IS_EMPTY"
    }
}