package com.verygoodsecurity.vgscollect.view.card.formatter.date

import android.text.Editable
import com.verygoodsecurity.vgscollect.util.extension.digits
import com.verygoodsecurity.vgscollect.view.date.DateRangeFormat
import com.verygoodsecurity.vgscollect.view.date.DatePickerMode

internal class FlexibleDateRangeFormatter : BaseDateFormatter() {

    //region - Properties
    private var dateFormat = DateRangeFormat.MM_DD_YYYY
    private var mode: DatePickerMode = DatePickerMode.INPUT
    private var runtimeData = ""
    //endregion

    //region - Overrides
    override fun setMode(mode: DatePickerMode) {
        this.mode = mode
    }

    override fun getMask(): String = dateFormat.formatPattern

    override fun setMask(mask: String) {
        val parsedFormat = DateRangeFormat.parsePatternToDateFormat(mask)
        dateFormat = if (parsedFormat != null) {
            parsedFormat
        } else {
            DateRangeFormat.MM_DD_YYYY
        }
    }
    //endregion

    //region - TextWatcher implementation
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // Do nothing
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        do {
            val primaryStr = runtimeData
            formatString(s.toString())
        } while (primaryStr != runtimeData)
    }

    override fun afterTextChanged(s: Editable?) {
        if (mode == DatePickerMode.INPUT) {
            s?.apply {
                if (s.toString() != runtimeData) {
                    replace(0, s.length, runtimeData)
                }
            }
        }
    }
    //endregion

    //region - Private methods
    private fun formatString(str: String) {
        val text = str.digits
        val textCount = if (dateFormat.size < text.length) {
            dateFormat.size
        } else {
            text.length
        }

        val builder = StringBuilder()
        var indexSpace = 0

        repeat(textCount) { charIndex ->
            val maskIndex = charIndex + indexSpace

            if (maskIndex < dateFormat.formatPattern.length) {
                val maskChar = dateFormat.formatPattern[maskIndex]
                val char = text[charIndex]

                if (maskChar == dateFormat.formatPatternItem) {
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
    //endregion
}