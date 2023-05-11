package com.verygoodsecurity.vgscollect.view.date.validation

import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator
import com.verygoodsecurity.vgscollect.view.date.formatter.VGSDateFormat
import java.util.*

internal class DateRangeValidator(
    private val dateFormat: VGSDateFormat,
    private val startDate: Date?,
    private val endDate: Date?
) : VGSValidator {

    //region - VGSValidator implementation
    override val errorMsg: String = DEFAULT_ERROR_MSG

    override fun isValid(content: String): Boolean {
        // Must have valid input
        if (content.isEmpty()) {
            return false
        }

        // Format input date match selected format
        val inputDate = dateFormat.dateFromString(content) ?: return false

        // When startDate and endDate are set, validate that startDate `<=` inputDate `<=` endDate
        if (startDate != null && endDate != null) {
            return inputDate.after(startDate) && inputDate.before(endDate)
        }

        // When startDate is set, validate that startDate `<=` inputDate
        if (startDate != null) {
            return inputDate.after(startDate)
        }

        // When endDate is set, validate that inputDate `<=` endDate
        if (endDate != null) {
            return inputDate.before(endDate)
        }

        // Positive validation
        return true
    }
    //endregion

    //region - Companion
    internal companion object {
        internal const val DEFAULT_ERROR_MSG = "DATE_VALIDATION_ERROR"
    }
    //endregion
}