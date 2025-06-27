package com.verygoodsecurity.vgscollect.widget.compose.validator

import com.verygoodsecurity.vgscollect.widget.compose.date.VgsExpiryDateFormat
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidationResult
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidator
import java.text.SimpleDateFormat
import java.util.Locale

class VgsMinMaxDateValidator(
    val minDate: Long,
    val maxDate: Long,
    val inputDateFormat: VgsExpiryDateFormat,
    override val errorMsg: String = ERROR_MESSAGE
) : VgsTextFieldValidator() {

    companion object {

        const val ERROR_MESSAGE = "MIN_MAX_DATE_VALIDATION_ERROR"
    }

    override fun validate(text: String): Result {
        val formattedText = text.format(inputDateFormat.mask)
        val date = try {
            SimpleDateFormat(inputDateFormat.dateFormat, Locale.US).parse(formattedText)
        } catch (_: Exception) {
            null
        }
        return if (date != null && LongRange(minDate, maxDate).contains(date.time)) {
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