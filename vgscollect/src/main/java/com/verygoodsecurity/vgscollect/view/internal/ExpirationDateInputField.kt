package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.date.DateFormat

internal class ExpirationDateInputField(context: Context) : DateInputField(context) {

    override var fieldType: FieldType = FieldType.CARD_EXPIRATION_DATE

    override var inclusiveRangeValidation: Boolean = false

    override var inputDateFormat: DateFormat = DateFormat.MM_YYYY

    override var datePickerMinDate: Long? = null

    override var datePickerMaxDate: Long? = null
}