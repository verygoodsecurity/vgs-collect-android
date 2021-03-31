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

    override fun isValid(content: String?): Boolean {
        val str = content?.trim()
        if(str.isNullOrEmpty()) {
            return false
        }
        try {
            val date = sdf.parse(str).time
            calendar.timeInMillis = date
            val dayLast = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            calendar.set(Calendar.DAY_OF_MONTH, dayLast)
        } catch (e: ParseException) {
            return false
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

        return isBiggerThanMin && isLowerThanMax
    }
}