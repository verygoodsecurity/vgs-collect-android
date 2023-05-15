package com.verygoodsecurity.vgscollect.widget

import android.content.Context
import android.util.AttributeSet
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.core.model.VGSDate
import com.verygoodsecurity.vgscollect.core.model.setMaximumTime
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.date.VGSDateFormat
import com.verygoodsecurity.vgscollect.widget.core.DateEditText
import java.util.*

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
                val inputFormat = VGSDateFormat.parsePatternToDateFormat(getDateFormat())
                val calendar = Calendar.getInstance()

                val startDateValue = getString(R.styleable.RangeDateEditText_startDate)
                val startDate = inputFormat?.dateFromString(startDateValue)
                if (startDate != null) {
                    setMinDate(
                        calendar.apply {
                            time = startDate
                            setMaximumTime()
                        }.timeInMillis
                    )
                }

                val endDateValue = getString(R.styleable.RangeDateEditText_endDate)
                val endDate = inputFormat?.dateFromString(endDateValue)
                if (endDate != null) {
                    setMaxDate(
                        calendar.apply {
                            time = endDate
                            setMaximumTime()
                        }.timeInMillis
                    )
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

    /**
     * Set maximum date allowed
     */
    fun setMaxDate(date: VGSDate) {
        setMaxDate(date.timeInMillis)
    }
}
