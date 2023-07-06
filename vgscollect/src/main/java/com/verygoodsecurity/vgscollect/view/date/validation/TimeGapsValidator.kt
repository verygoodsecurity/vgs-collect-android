package com.verygoodsecurity.vgscollect.view.date.validation

import com.verygoodsecurity.vgscollect.util.extension.setMaximumTime
import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/** @suppress */
internal class TimeGapsValidator(
    private val pattern: String,
    private val daysVisible: Boolean,
    private val inclusive: Boolean,
    private val minDate: Long? = null,
    private val maxDate: Long? = null
) : VGSValidator {

    override val errorMsg: String = DEFAULT_ERROR_MSG

    private val sdf by lazy {
        val sdf = SimpleDateFormat(pattern, Locale.US)
        sdf.isLenient = false
        sdf
    }

    private val calendar = Calendar.getInstance()

    override fun isValid(content: String): Boolean {
        val str = content.trim()
        if (str.isEmpty()) {
            return false
        }

        try {
            val date = sdf.parse(str)
            if (date != null) {
                calendar.apply {
                    timeInMillis = date.time
                    if (!daysVisible) {
                        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                    }
                    setMaximumTime()
                }
            } else {
                return false
            }
        } catch (e: ParseException) {
            return false
        }

        val isBiggerThanMin: Boolean = minDate?.run {
            if (inclusive) {
                calendar.timeInMillis >= this
            } else {
                calendar.timeInMillis > this
            }
        } ?: true

        val isLowerThanMax:Boolean = maxDate?.run {
            if (inclusive) {
                calendar.timeInMillis <= this
            } else {
                calendar.timeInMillis < this
            }
        } ?: true

        return isBiggerThanMin && isLowerThanMax
    }

    internal companion object {
        internal const val DEFAULT_ERROR_MSG = "EXPIRATION_DATE_VALIDATION_ERROR"
    }
}