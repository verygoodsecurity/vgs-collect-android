package com.verygoodsecurity.vgscollect.view.card.validation.payment.brand

import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator

/** @suppress */
class LuhnCheckSumValidator constructor(
    private val errorMsg: String = DEFAULT_ERROR_MSG
) : VGSValidator {

    override fun isValid(content: String): String? {
        return if (content.isNotEmpty() && isLuhnCheckSumValid(content)) null else errorMsg
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

    private companion object {

        private const val DEFAULT_ERROR_MSG = "LUHN_ALGORITHM_CHECK_VALIDATION_ERROR"
    }
}