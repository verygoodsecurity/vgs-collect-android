package com.verygoodsecurity.vgscollect.widget

import android.content.Context
import android.util.AttributeSet
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.formatter.rules.FormatMode

class DateRangeEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : InputFieldView(context, attrs, defStyleAttr) {

    init {
        setupViewType(FieldType.DATE_RANGE)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.DateRangeEditText,
            0,
            0
        ).apply {
            val formatterMode = getInt(
                R.styleable.DateRangeEditText_rangeFormatterMode,
                FormatMode.STRICT.ordinal
            )

            // Get date input and output patterns
            val datePattern = getString(R.styleable.DateRangeEditText_datePattern)
            setDatePattern(datePattern)

            val outputPattern = getString(R.styleable.DateRangeEditText_outputPattern)

            val datePickerMode = getInt(R.styleable.DateRangeEditText_datePickerModes, 1)
            setDatePickerMode(datePickerMode)

            val hint = getString(R.styleable.DateRangeEditText_hint)
            setHint(hint)

//            val outputPattern = getString(R.styleable.DateRangeEditText_outputPattern)
//            val datePickerMode = getInt(R.styleable.DateRangeEditText_datePickerModes, 1)
        }
    }
}