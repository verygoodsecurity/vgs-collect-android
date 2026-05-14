package com.verygoodsecurity.vgscollect.widget.compose.validator

import com.verygoodsecurity.vgscollect.widget.compose.date.VgsExpiryDateFormat
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidationResult
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidator

/**
 * Validator that requires the parsed date to fall within an inclusive range.
 *
 * Useful for card expiry fields to reject dates in the past or far in the future.
 *
 * @param minDate earliest accepted date as a Unix timestamp in milliseconds.
 * @param maxDate latest accepted date as a Unix timestamp in milliseconds.
 * @param dateFormat the format used to parse the field's text.
 * @param errorMsg message returned when the date is outside the range or unparsable.
 */
class VgsMinMaxDateValidator(
    minDate: Long,
    maxDate: Long,
    val dateFormat: VgsExpiryDateFormat,
    override val errorMsg: String = ERROR_MESSAGE
) : VgsTextFieldValidator() {

    val range = LongRange(minDate, maxDate)

    companion object {

        const val ERROR_MESSAGE = "MIN_MAX_DATE_VALIDATION_ERROR"
    }

    override fun validate(text: String): Result {
        val date = dateFormat.parse(text)
        return if (date != null && range.contains(date.time)) {
            Result(true)
        } else {
            Result(false, errorMsg)
        }
    }

    class Result(
        override val isValid: Boolean,
        override val errorMsg: String? = null
    ) : VgsTextFieldValidationResult()
}