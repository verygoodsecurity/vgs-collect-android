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
import java.text.ParseException
import java.text.SimpleDateFormat
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

    /**
     * Interface definition for a callback to be invoked when the DatePicker Dialog changes
     * visibility.
     */
    interface OnDatePickerVisibilityChangeListener : VisibilityChangeListener

    private fun parseRange(rangeDate: String?, format: String?): Date? {
        val simpleDateFormat = SimpleDateFormat(format, Locale.US).apply { isLenient = true }
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)

        return try {
            val inputDate = rangeDate?.let { simpleDateFormat.parse(it) }
            inputDate?.let { calendar.time = inputDate }
            if (format?.contains(char = 'y', ignoreCase = true) == false) {
                calendar.set(Calendar.YEAR, currentYear)
            }
            println("TEST:DD range = $rangeDate, result = ${calendar.time}")
            calendar.time
        } catch (e: ParseException) {
            println("TEST:DD null")
            // Log
            null
        }
    }
}
