package com.verygoodsecurity.vgscollect.widget

import android.content.Context
import android.util.AttributeSet
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.core.model.VGSDate
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.formatter.rules.FormatMode
import com.verygoodsecurity.vgscollect.view.date.VGSDateFormat
import com.verygoodsecurity.vgscollect.widget.core.DateEditText

class RangeDateEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : DateEditText(FieldType.DATE_RANGE, context, attrs, defStyleAttr) {

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.RangeDateEditText,
            0, 0
        ).apply {
            try {
                val inputFormatValue = getDateFormat()
                val inputFormat = VGSDateFormat.parsePatternToDateFormat(inputFormatValue)

                val startDateValue = getString(R.styleable.RangeDateEditText_startDate)
                val startDate = inputFormat?.dateFromString(startDateValue)
                if (startDate != null) {
                    setMinDate(startDate.time)
                }

                val endDateValue = getString(R.styleable.RangeDateEditText_endDate)
                val endDate = inputFormat?.dateFromString(endDateValue)
                if (endDate != null) {
                    setMaxDate(endDate.time)
                }
            } finally {
                recycle()
            }
        }
    }

    /**
     * Set minimum date allowed
     */
    fun setMinDate(date: VGSDate) {
        setMinDate(date.timeInMillis)
    }

    fun setMaxDate(date: VGSDate) {
        setMaxDate(date.timeInMillis)
    }
}
