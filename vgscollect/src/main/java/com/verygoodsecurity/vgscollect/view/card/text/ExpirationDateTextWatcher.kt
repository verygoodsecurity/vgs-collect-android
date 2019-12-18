package com.verygoodsecurity.vgscollect.view.card.text

import android.text.Editable
import android.text.TextWatcher
import java.util.regex.Pattern

object ExpirationDateTextWatcher: TextWatcher {
    private const val MOUNTS_PATTERN = "^([10]|0[1-9]|1[012])\$"

    private val patternMounts = Pattern.compile(MOUNTS_PATTERN)

    private const val TOTAL_SYMBOLS = 7
    private const val TOTAL_DIGITS = 6
    private const val DIVIDER_MODULO = 3
    private const val DIVIDER_POSITION = DIVIDER_MODULO - 1
    private const val DIVIDER = '/'

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(s: Editable) {
        if (!isInputCorrect(
                s,
                TOTAL_SYMBOLS,
                DIVIDER_MODULO,
                DIVIDER
            )
        ) {
            s.replace(0, s.length,
                buildCorrectString(
                    getDigitArray(
                        s,
                        TOTAL_DIGITS
                    ),
                    DIVIDER_POSITION,
                    DIVIDER
                )
            )
        }
    }

    private fun isInputCorrect(s: Editable, totalSymbols:Int, dividerModulo:Int, divider:Char):Boolean {
        var isCorrect = s.length <= totalSymbols
        for (i in s.indices) {
            isCorrect = if (i > 0 && (i + 1) % dividerModulo == 0 && i <= DIVIDER_MODULO) {
                isCorrect and (divider == s[i])
            } else if (i == 0) {
                isCorrect and Character.isDigit(s[i]) and (s[i].toInt() == 48 || s[i].toInt() == 49)
            } else {
                isCorrect and Character.isDigit(s[i])
            }
        }

        if(s.length == 2) {
            isCorrect = isCorrect && patternMounts.matcher(s).matches()
        }

        return isCorrect
    }

    private fun buildCorrectString(digits: CharArray, dividerPosition: Int, divider: Char): String {
        val formatted = StringBuilder()

        for (i in digits.indices) {
            if (digits[i].toInt() != 0) {
                if(i == 0 && digits[i].toInt() !in 48..49) {
                    formatted.append("0")
                }
                formatted.append(digits[i])
                if (i in 1 until DIVIDER_MODULO && (i + 1) % dividerPosition == 0) {
                    formatted.append(divider)
                }
            }
        }

        if(formatted.length <= 3) {
            val s = formatted.split(divider)
            val iss = patternMounts.matcher(s[0]).matches()
            if (!iss) {
                return digits.first().toString()
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