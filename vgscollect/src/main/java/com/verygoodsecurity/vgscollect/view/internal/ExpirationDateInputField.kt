package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.date.DateFormat
import com.verygoodsecurity.vgscollect.view.internal.core.DateInputField

internal class ExpirationDateInputField(context: Context) : DateInputField(context) {

    override var fieldType: FieldType = FieldType.CARD_EXPIRATION_DATE

    override var inclusiveRangeValidation: Boolean = false

    override var validDateFormats = listOf(DateFormat.MM_YY, DateFormat.MM_YYYY)

    override var inputDateFormat: DateFormat = DateFormat.MM_YYYY

    override var datePickerMinDate: Long? = null

    override var datePickerMaxDate: Long? = null
}