package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.internal.core.DateInputField

internal class ExpirationDateInputField(context: Context) : DateInputField(context) {

    //region - Abstract implementation
    override var fieldType: FieldType = FieldType.CARD_EXPIRATION_DATE
    override var inclusiveRangeValidation: Boolean = false
    override var inputDatePattern: String = MM_YYYY
    override var datePickerMinDate: Long? = null
    override var datePickerMaxDate: Long? = null
    //endregion

    //region - Companion
    companion object {
        const val MM_YYYY = "MM/yyyy"
    }
    //endregion
}