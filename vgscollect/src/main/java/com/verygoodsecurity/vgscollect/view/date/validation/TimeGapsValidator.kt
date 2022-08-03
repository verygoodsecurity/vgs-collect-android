package com.verygoodsecurity.vgscollect.view.date.validation

import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/** @suppress */
internal class TimeGapsValidator(
    pattern:String,
    private val minDate:Long? = null,
    private val maxDate:Long? = null
) : VGSValidator {
    private val sdf by lazy {
        val sdf = SimpleDateFormat(pattern, Locale.US)
        sdf.isLenient = false
        sdf
    }
    private val calendar = Calendar.getInstance()

    override fun isValid(content: String): String? {
        val str = content.trim()
        if(str.isEmpty()) {
            return ERROR_MSG
        }
        try {
            val date = sdf.parse(str).time
            calendar.timeInMillis = date
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            calendar.set(Calendar.HOUR_OF_DAY,  calendar.getActualMaximum(Calendar.HOUR_OF_DAY))
            calendar.set(Calendar.MINUTE,  calendar.getActualMaximum(Calendar.MINUTE))
            calendar.set(Calendar.SECOND,  calendar.getActualMaximum(Calendar.SECOND))
            calendar.set(Calendar.MILLISECOND,  calendar.getActualMaximum(Calendar.MILLISECOND))
        } catch (e: ParseException) {
            return ERROR_MSG
        }

        val isBiggerThanMin:Boolean = minDate?.run {
            calendar.timeInMillis > this
        }?:true

        val isLowerThanMax:Boolean = maxDate?.run {
            if(minDate != null && minDate < this || minDate == null) {
                calendar.timeInMillis < this
            } else {
                true
            }
        }?:true

        return if (isBiggerThanMin && isLowerThanMax) null else ERROR_MSG
    }

    private companion object {

        private const val ERROR_MSG = "EXPIRATION_DATE_VALIDATION_ERROR"
    }
}