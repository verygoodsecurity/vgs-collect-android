package com.verygoodsecurity.vgscollect.view.card.validation

import java.util.regex.Pattern

/** @suppress */
class RegexValidator(
    internal val value: String,
    private val errorMsg: String = DEFAULT_ERROR_MSG
) : VGSValidator {

    private var pattern: Pattern = Pattern.compile(value)

    override fun isValid(content: String): String? {
        return if (content.isEmpty() || pattern.matcher(content).matches()) {
            null
        } else {
            errorMsg
        }
    }

    internal companion object {

        internal const val DEFAULT_ERROR_MSG = "REGEX_VALIDATION_ERROR"
    }
}