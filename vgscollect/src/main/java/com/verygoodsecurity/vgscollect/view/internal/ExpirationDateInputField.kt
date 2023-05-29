package com.verygoodsecurity.vgscollect.view.internal

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.View
import android.view.autofill.AutofillValue
import com.verygoodsecurity.vgscollect.util.extension.setMaximumTime
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.internal.core.DateInputField
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

internal class ExpirationDateInputField(context: Context) : DateInputField(context) {

    //region - Abstract implementation
    override var fieldType: FieldType = FieldType.CARD_EXPIRATION_DATE
    override var inclusiveRangeValidation: Boolean = false
    override var inputDatePattern: String = MM_YYYY
    override var datePickerMinDate: Long? = null
    override var datePickerMaxDate: Long? = null

    override fun setupAutofill() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setAutofillHints(
                View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DATE,
                View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_MONTH,
                View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DAY,
                View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_YEAR
            )
        }
    }

    override fun autofill(value: AutofillValue?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            when {
                value == null -> {}
                value.isDate -> selectedDate.time = Date(value.dateValue)
                value.isText -> {
                    val newValue = parseTextDate(value)
                    super.autofill(newValue)
                }
                else -> {
                    super.autofill(value)
                }
            }
        }
    }
    //endregion

    //region - Private methods
    @SuppressLint("NewApi")
    private fun parseTextDate(value: AutofillValue): AutofillValue {
        val str = value.textValue.toString()
        return if (str.length == inputDatePattern.length) {
            value
        } else {
            val newDateStr = value.textValue.toString().handleDate("MM/yy", inputDatePattern)
            if (newDateStr.isNullOrEmpty()) {
                value
            } else {
                AutofillValue.forText(newDateStr)
            }
        }
    }

    private fun String.handleDate(incomePattern: String, outcomePattern: String): String? {
        return try {
            val income = SimpleDateFormat(incomePattern, Locale.US)
            val currentDate = income.parse(this)
            val selectedDate = Calendar.getInstance()
            if (currentDate != null) {
                selectedDate.time = currentDate
            }
            if (!isDaysVisible) {
                selectedDate.set(
                    Calendar.DAY_OF_MONTH,
                    selectedDate.getActualMaximum(Calendar.DATE)
                )
            }
            selectedDate.setMaximumTime()
            val outcome = SimpleDateFormat(outcomePattern, Locale.US)
            outcome.format(selectedDate.time)
        } catch (e: ParseException) {
            null
        }
    }
    //endregion

    //region - Companion
    companion object {
        const val MM_YYYY = "MM/yyyy"
    }
    //endregion
}