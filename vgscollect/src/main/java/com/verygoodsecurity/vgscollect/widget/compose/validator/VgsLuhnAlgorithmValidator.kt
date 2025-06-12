package com.verygoodsecurity.vgscollect.widget.compose.validator

import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidationResult
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidator

class VgsLuhnAlgorithmValidator(override val errorMsg: String = ERROR_MESSAGE) :
    VgsTextFieldValidator() {

    companion object {

        internal const val ERROR_MESSAGE = "LUHN_ALGORITHM_CHECK_VALIDATION_ERROR"
    }

    override fun validate(text: String): VgsTextFieldValidationResult {
        val isValid = text.isNotBlank() && isLuhnCheckSumValid(text)
        return Result(isValid = isValid, errorMsg = if (isValid) null else errorMsg)
    }

    private fun isLuhnCheckSumValid(number: String): Boolean {
        var cardSum = 0
        var isDoubled = false
        for (i in number.length - 1 downTo 0) {
            val digit = number[i] - '0'
            if (digit < 0 || digit > 9) {
                continue
            }
            var append: Int
            if (isDoubled) {
                append = digit * 2
                if (append > 9) {
                    append -= 9
                }
            } else {
                append = digit
            }
            cardSum += append
            isDoubled = !isDoubled
        }
        return cardSum % 10 == 0
    }

    class Result(
        override val isValid: Boolean,
        override val errorMsg: String? = null
    ) : VgsTextFieldValidationResult()
}