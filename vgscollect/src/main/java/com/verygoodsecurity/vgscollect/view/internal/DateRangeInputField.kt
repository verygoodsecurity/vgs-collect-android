package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import com.verygoodsecurity.vgscollect.util.extension.setMaximumTime
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.date.DateRangeFormat
import com.verygoodsecurity.vgscollect.view.internal.core.DateInputField
import java.util.Calendar
import kotlin.properties.Delegates

internal class DateRangeInputField(context: Context) : DateInputField(context) {

    //region - Abstract implementation
    override var fieldType: FieldType = FieldType.DATE_RANGE
    override var inclusiveRangeValidation: Boolean = true
    override var inputDatePattern: String by Delegates.observable(DateRangeFormat.MMddYYYY.format) { _, _, new ->
        DateRangeFormat.parsePatternToDateFormat(new)?.let {
            isDaysVisible = it != DateRangeFormat.MMyy
        }
    }

    override var datePickerMinDate: Long? = Calendar.getInstance().apply {
        set(Calendar.YEAR, this.get(Calendar.YEAR) - 100)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.MONTH, 0)
        setMaximumTime()
    }.timeInMillis

    override var datePickerMaxDate: Long? = Calendar.getInstance().apply {
        set(Calendar.YEAR, this.get(Calendar.YEAR) + 100)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.MONTH, 0)
        setMaximumTime()
    }.timeInMillis

    override var isDaysVisible: Boolean = true

    override var minDate: Long? = null
        set(value) {
            field = value
            updateSelectedDate()
            updateTimeGapsValidator()
        }

    override var maxDate: Long? = null
        set(value) {
            field = value
            updateSelectedDate()
            updateTimeGapsValidator()
        }

    override fun validateDatePattern(pattern: String?): String {
        return DateRangeFormat.parsePatternToDateFormat(pattern)?.format ?: inputDatePattern
    }
    //endregion

    private fun updateSelectedDate() {
        val min = minDate
        val max = maxDate
        val selected = selectedDate.timeInMillis
        if (min != null && selected < min) {
            selectedDate.timeInMillis = min
        } else if (max != null && selected > max) {
            selectedDate.timeInMillis = max
        }
    }
}