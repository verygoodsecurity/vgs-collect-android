package com.verygoodsecurity.vgscollect.view.card.validation

/** @suppress */
class InfoValidator : VGSValidator {

    override fun isValid(content: String): String? {
        return if (content.trim().isEmpty()) ERROR_MESSAGE else null
    }

    private companion object {

        private const val ERROR_MESSAGE = "INFO_FIELD_IS_EMPTY"
    }
}