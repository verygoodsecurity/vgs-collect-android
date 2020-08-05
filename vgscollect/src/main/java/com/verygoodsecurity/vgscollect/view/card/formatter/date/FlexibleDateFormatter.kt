package com.verygoodsecurity.vgscollect.view.card.formatter.date

import android.text.Editable
import com.verygoodsecurity.vgscollect.view.date.DatePickerMode
import java.lang.StringBuilder

class FlexibleDateFormatter : BaseDateFormatter() {

    companion object {
        private const val DATE_PATTERN = "MM/yyyy"
        private const val DATE_FORMAT = "##/####"

        private const val NUMBER_REGEX = "[^\\d]"
        private const val MASK_ITEM = '#'
    }

    private var mask:String = DATE_FORMAT
    private var pattern:String = DATE_PATTERN
    private var maxLength = DATE_FORMAT.length

    private var runtimeData = ""

    private var mode:DatePickerMode = DatePickerMode.INPUT

    override fun afterTextChanged(s: Editable?) {
        if(mode == DatePickerMode.INPUT) {
            s?.apply {
                if(s.toString() != runtimeData) {
                    replace(0, s.length, runtimeData)
                }
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
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

    override fun setMode(mode: DatePickerMode) {
        this.mode = mode
    }

    override fun setMask(mask: String) {
        this.mask = mask
            .replace("M", "#", true)
            .replace("y", "#", true)
        this.pattern = mask
    }

    override fun getMask(): String = mask

}