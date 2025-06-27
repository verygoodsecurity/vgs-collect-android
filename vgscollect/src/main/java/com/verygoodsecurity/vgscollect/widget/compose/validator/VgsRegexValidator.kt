package com.verygoodsecurity.vgscollect.widget.compose.validator

import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidationResult
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidator
import java.util.regex.Pattern

class VgsRegexValidator(
    val regex: String,
    override val errorMsg: String = ERROR_MESSAGE
) : VgsTextFieldValidator() {

    private var pattern: Pattern = Pattern.compile(regex)

    companion object {

        const val ERROR_MESSAGE = "REGEX_VALIDATION_ERROR"
    }

    override fun validate(text: String): Result {
        return if (pattern.matcher(text).matches()) {
            Result(isValid = true, errorMsg = null)
        } else {
            Result(isValid = false, errorMsg = errorMsg)
        }
    }

    class Result(
        override val isValid: Boolean,
        override val errorMsg: String? = null
    ) : VgsTextFieldValidationResult()
}