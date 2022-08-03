package com.verygoodsecurity.vgscollect.view.card.validation

import java.util.regex.Pattern

/** @suppress */
class RegexValidator(
    internal val value: String,
    override val errorMsg: String = DEFAULT_ERROR_MSG
) : VGSValidator {

    private var pattern: Pattern = Pattern.compile(value)

    override fun isValid(content: String): Boolean {
        return pattern.matcher(content).matches()
    }

    internal companion object {

        internal const val DEFAULT_ERROR_MSG = "REGEX_VALIDATION_ERROR"
    }
}