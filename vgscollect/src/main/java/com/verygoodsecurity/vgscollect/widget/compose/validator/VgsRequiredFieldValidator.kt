package com.verygoodsecurity.vgscollect.widget.compose.validator

import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidationResult
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidator

const val DEFAULT_REQUIRED_FILED_VALIDATOR_ERROR_MSG = "This field is required."

class VgsRequiredFieldValidator(
    override val errorMsg: String? = DEFAULT_REQUIRED_FILED_VALIDATOR_ERROR_MSG
) : VgsTextFieldValidator() {

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