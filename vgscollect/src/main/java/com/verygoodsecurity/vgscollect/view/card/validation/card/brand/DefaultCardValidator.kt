package com.verygoodsecurity.vgscollect.view.card.validation.card.brand

import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator
import java.util.regex.Pattern

/** @suppress */
class DefaultCardValidator(
    regex:String = ""
): VGSValidator {
    private val m = Pattern.compile(regex)

    override fun isValid(content: String?): Boolean {
        return !content.isNullOrEmpty() &&
                m.matcher(content).matches()
    }
}

internal fun VGSValidator.isLuhnCheckSumValid(number: String): Boolean {
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
