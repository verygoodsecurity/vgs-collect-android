package com.verygoodsecurity.vgscollect.view.text.validation.card

import android.text.Editable
import android.text.TextWatcher

object ExpirationDateeTextwatcher: TextWatcher {
    private const val TOTAL_SYMBOLS = 5 // size of pattern 0000-0000-0000-0000
    private const val TOTAL_DIGITS = 4 // max numbers of digits in pattern: 0000 x 4
    private const val DIVIDER_MODULO = 3 // means divider position is every 5th symbol beginning with 1
    private const val DIVIDER_POSITION = DIVIDER_MODULO - 1 // means divider position is every 4th symbol beginning with 0
    private const val DIVIDER = '/'

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(s: Editable) {
        if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
            s.replace(0, s.length, buildCorrectString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER));
        }
    }

    private fun isInputCorrect(s: Editable, totalSymbols:Int, dividerModulo:Int, divider:Char):Boolean {
        var isCorrect = s.length <= totalSymbols // check size of entered string
        for (i in s.indices) { // check that every element is right
            isCorrect = if (i > 0 && (i + 1) % dividerModulo == 0) {
                isCorrect and (divider == s[i])
            } else if (i == 0) {
                isCorrect and Character.isDigit(s[i])
            } else if (i == 1) {
                isCorrect and Character.isDigit(s[i]) and (i in 1..2)
            } else {
                isCorrect and Character.isDigit(s[i])
            }
        }
        return isCorrect
    }

    private fun buildCorrectString(digits: CharArray, dividerPosition: Int, divider: Char): String {
        val formatted = StringBuilder()

        for (i in digits.indices) {
            if (digits[i].toInt() != 0) {
                formatted.append(digits[i])
                if (i > 0 && i < digits.size - 1 && (i + 1) % dividerPosition == 0) {
                    formatted.append(divider)
                }
            }
        }

        return formatted.toString()
    }

    private fun getDigitArray(s: Editable, size: Int): CharArray {
        val digits = CharArray(size)
        var index = 0
        var i = 0
        while (i < s.length && index < size) {
            val current = s[i]
            if (Character.isDigit(current)) {
                digits[index] = current
                index++
            }
            i++
        }
        return digits
    }


}