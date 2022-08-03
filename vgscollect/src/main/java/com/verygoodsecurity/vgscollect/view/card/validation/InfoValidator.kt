package com.verygoodsecurity.vgscollect.view.card.validation

/** @suppress */
class InfoValidator : VGSValidator {

    override val errorMsg: String = ERROR_MESSAGE

    override fun isValid(content: String) = content.trim().isNotEmpty()

    private companion object {

        private const val ERROR_MESSAGE = "INFO_FIELD_IS_EMPTY"
    }
}