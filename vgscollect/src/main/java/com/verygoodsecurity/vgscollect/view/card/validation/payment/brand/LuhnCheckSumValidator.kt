package com.verygoodsecurity.vgscollect.view.card.validation.payment.brand

import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator

/** @suppress */
class LuhnCheckSumValidator : VGSValidator {

    override fun isValid(content: String?): Boolean {
        return !content.isNullOrEmpty() && isLuhnCheckSumValid(content)
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
}