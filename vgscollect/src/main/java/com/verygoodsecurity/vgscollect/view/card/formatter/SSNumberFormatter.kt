package com.verygoodsecurity.vgscollect.view.card.formatter

import android.text.Editable
import android.text.TextWatcher
import androidx.annotation.VisibleForTesting
import java.lang.StringBuilder

class SSNumberFormatter: TextWatcher, Formatter {

    companion object {
        private const val DEFAULT_MASK = "###-##-####"
        private const val DEFAULT_LENGTH = 11

        private const val NUMBER_REGEX = "[^\\d]"

        private const val MASK_ITEM = '#'
    }

    private var mask:String = DEFAULT_MASK
    private var maxLength = DEFAULT_LENGTH

    private var runtimeData = ""

    override fun setMask(mask: String) {
        this.mask = mask
        maxLength = mask.length
    }

    override fun afterTextChanged(s: Editable?) {
        s?.apply {
            if(s.toString() != runtimeData) {
                replace(0, s.length, runtimeData)
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        do {
            val primaryStr = runtimeData
            formatString(s.toString())
        } while (primaryStr != runtimeData)
    }

    private fun formatString(str: String) {
        val text = str.replace(Regex(NUMBER_REGEX), "")

        val textCount = if(maxLength < text.length) {
            maxLength
        } else {
            text.length
        }

        val builder = StringBuilder()
        var indexSpace = 0

        repeat(textCount) { charIndex ->
            val maskIndex = charIndex+indexSpace

            if(maskIndex < mask.length) {
                val maskChar = mask[maskIndex]
                val char = text[charIndex]

                if (maskChar == MASK_ITEM) {
                    builder.append(char)
                } else {
                    indexSpace += 1
                    builder.append(maskChar)
                    if (char.isDigit()) {
                        builder.append(char)
                    }
                }
            }
        }

        runtimeData = builder.toString()
    }

    override fun getMask():String = mask

    @VisibleForTesting
    fun getMaskLength():Int = maxLength
}