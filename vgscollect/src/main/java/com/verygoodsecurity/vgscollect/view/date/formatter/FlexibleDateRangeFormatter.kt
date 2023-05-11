package com.verygoodsecurity.vgscollect.view.date.formatter

import android.text.Editable
import com.verygoodsecurity.vgscollect.util.extension.digits
import com.verygoodsecurity.vgscollect.view.date.DatePickerMode

internal class FlexibleDateRangeFormatter(
    override var dateFormatter: VGSDateFormat,
    override var pickerMode: DatePickerMode
) : BaseDateRangeFormatter(), DateRangePickerFormatter {

    //region - Properties
    private var runtimeData = ""
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
        if (pickerMode == DatePickerMode.INPUT) {
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
        val textCount = if (dateFormatter.size < text.length) {
            dateFormatter.size
        } else {
            text.length
        }

        val builder = StringBuilder()
        var indexSpace = 0

        repeat(textCount) { charIndex ->
            val maskIndex = charIndex + indexSpace

            if (maskIndex < dateFormatter.pattern.length) {
                val maskChar = dateFormatter.pattern[maskIndex]
                val char = text[charIndex]

                if (maskChar == dateFormatter.patternItem) {
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