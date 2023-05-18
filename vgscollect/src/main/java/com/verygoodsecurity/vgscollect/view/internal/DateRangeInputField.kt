package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import com.verygoodsecurity.vgscollect.core.model.setMaximumTime
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.date.DateRangeFormat
import com.verygoodsecurity.vgscollect.view.internal.core.DateInputField
import java.util.*

internal class DateRangeInputField(context: Context) : DateInputField(context) {

    //region - Abstract implementation
    override var fieldType: FieldType = FieldType.DATE_RANGE
    override var inclusiveRangeValidation: Boolean = true
    override var inputDatePattern = DateRangeFormat.MM_DD_YYYY.format

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
    //endregion
}