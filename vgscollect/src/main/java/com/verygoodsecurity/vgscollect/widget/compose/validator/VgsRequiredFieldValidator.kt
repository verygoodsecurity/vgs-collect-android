package com.verygoodsecurity.vgscollect.widget.compose.validator

import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidationResult
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidator

/**
 * Validator that fails when the field is blank.
 *
 * @param errorMsg message returned when the field is empty or whitespace-only.
 */
class VgsRequiredFieldValidator(
    override val errorMsg: String = ERROR_MESSAGE
) : VgsTextFieldValidator() {

    companion object {

        const val ERROR_MESSAGE = "REQUIRED_FIELD_VALIDATION_ERROR"
    }

    override fun validate(text: String): Result {
        return if (text.isBlank()) {
            Result(isValid = false, errorMsg = errorMsg)
        } else {
            Result(isValid = true)
        }
    }

    class Result(
        override val isValid: Boolean,
        override val errorMsg: String? = null
    ) : VgsTextFieldValidationResult()
}