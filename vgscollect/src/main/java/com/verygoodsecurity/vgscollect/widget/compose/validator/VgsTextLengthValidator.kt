package com.verygoodsecurity.vgscollect.widget.compose.validator

import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidationResult
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidator

/**
 * Validator that requires the value's length to match one of the allowed [lengths].
 *
 * Useful for fixed-length identifiers (e.g. `arrayOf(9)` for SSN, `arrayOf(15, 16)` for card numbers).
 *
 * @param lengths the set of accepted character counts.
 * @param errorMsg message returned when the length is not in [lengths].
 */
class VgsTextLengthValidator(
    val lengths: Array<Int>,
    override val errorMsg: String = ERROR_MESSAGE
) : VgsTextFieldValidator() {

    companion object {
        const val ERROR_MESSAGE = "TEXT_LENGTH_VALIDATION_ERROR"
    }

    override fun validate(text: String): Result {
        val isValid = lengths.contains(text.length)
        return Result(isValid = isValid, errorMsg = if (isValid) null else errorMsg)
    }

    class Result(
        override val isValid: Boolean,
        override val errorMsg: String? = null
    ) : VgsTextFieldValidationResult()
}