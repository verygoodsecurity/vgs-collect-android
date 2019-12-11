package com.verygoodsecurity.vgscollect.view.card.text

import android.text.Editable
import android.text.TextWatcher

class CardNumberTextWatcher(
    dividerStr:String? = " "
): TextWatcher {

    companion object {
        private const val TOTAL_SYMBOLS = 22 // 0000 0000 0000 0000
        private const val TOTAL_DIGITS = 19 // 0000 x 4
        private const val DIVIDER_MODULO = 5 // means divider position is every 5th symbol beginning with 1
        private const val DIVIDER_POSITION = DIVIDER_MODULO - 1
    }

    private var hasDivider = true
    private val divider:Char

    init {
        divider = dividerStr?.run {
            if(length != 1) {
                hasDivider = false
                ' '
            } else {
                hasDivider = true
                this[0]
            }
        }?:' '
    }


    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(s: Editable) {
        if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_MODULO, divider)) {
            s.replace(0, s.length,
                buildCorrectString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, divider)
            )
        }
    }

    private fun isInputCorrect(s: Editable, totalSymbols:Int, dividerModulo:Int, divider:Char):Boolean {
        var isCorrect = s.length <= totalSymbols
        for (i in s.indices) {
            isCorrect = when {
                !hasDivider -> isCorrect and Character.isDigit(s[i])
                i >= 15 -> isCorrect and Character.isDigit(s[i])
                i > 0 && (i + 1) % dividerModulo == 0 -> isCorrect and (divider == s[i])
                else -> isCorrect and Character.isDigit(s[i])
            }
        }
        return isCorrect
    }

    private fun buildCorrectString(digits: CharArray, dividerPosition: Int, divider: Char): String {
        val formatted = StringBuilder()

        for (i in digits.indices) {
            if (digits[i].toInt() != 0) {
                formatted.append(digits[i])
                if(!hasDivider || i < 15) {
                    if (i > 0 && i < digits.size - 1 && (i + 1) % dividerPosition == 0) {
                        formatted.append(divider)
                    }
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