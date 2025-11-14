package com.verygoodsecurity.vgscollect.widget

import android.content.Context
import android.util.AttributeSet
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.core.model.VGSDate
import com.verygoodsecurity.vgscollect.util.extension.setMaximumTime
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.date.DateRangeFormat
import com.verygoodsecurity.vgscollect.widget.core.DateEditText
import com.verygoodsecurity.vgscollect.widget.core.VisibilityChangeListener
import java.util.Calendar

/**
 * A user interface element for inputting a date range.
 */
class RangeDateEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : DateEditText(FieldType.DATE_RANGE, context, attrs, defStyleAttr) {

    init {
        isFocusable = true
        this.isFocusableInTouchMode = true
        this.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showDatePickerDialog()
            }
        }
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.RangeDateEditText,
            0, 0
        ).apply {
            try {
                val inputFormat = DateRangeFormat.parsePatternToDateFormat(getDatePattern())
                val calendar = Calendar.getInstance()

                val minDateValue = getString(R.styleable.RangeDateEditText_minDate)
                val minDate = inputFormat?.dateFromString(minDateValue)
                if (minDate != null) {
                    setMinDate(
                        calendar.apply {
                            time = minDate
                            setMaximumTime()
                        }.timeInMillis
                    )
                }

                val maxDateValue = getString(R.styleable.RangeDateEditText_maxDate)
                val maxDate = inputFormat?.dateFromString(maxDateValue)
                if (maxDate != null) {
                    setMaxDate(
                        calendar.apply {
                            time = maxDate
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
     * Sets the minimum date allowed.
     *
     * @param date The minimum date allowed.
     */
    fun setMinDate(date: VGSDate) {
        setMinDate(date.timeInMillis)
    }

    /**
     * Sets the maximum date allowed.
     *
     * @param date The maximum date allowed.
     */
    fun setMaxDate(date: VGSDate) {
        setMaxDate(date.timeInMillis)
    }

    /**
     * A listener for receiving notifications about the visibility of the date picker dialog.
     */
    interface OnDatePickerVisibilityChangeListener : VisibilityChangeListener
}
