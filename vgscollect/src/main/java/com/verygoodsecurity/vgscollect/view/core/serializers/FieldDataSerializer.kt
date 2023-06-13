package com.verygoodsecurity.vgscollect.view.core.serializers

import com.verygoodsecurity.vgscollect.core.api.client.extension.logException
import java.text.SimpleDateFormat
import java.util.*

abstract class FieldDataSerializer<P, R> {

    internal abstract fun serialize(params: P): R

    private lateinit var daySDF: SimpleDateFormat
    private lateinit var monthSDF: SimpleDateFormat
    private lateinit var yearSDF: SimpleDateFormat

    protected fun getDayFormat(dateFormat: String?): SimpleDateFormat {
        return if (this::daySDF.isInitialized) {
            daySDF
        } else {
            try {
                val dayFormat = when {
                    dateFormat?.contains("dd") == true -> "dd"
                    dateFormat?.contains("d") == true -> "d"
                    else -> DEFAULT_DAY_FORMAT
                }
                SimpleDateFormat(dayFormat, Locale.US)
            } catch (e: Exception) {
                logException(e)
                SimpleDateFormat(DEFAULT_MONTH_FORMAT, Locale.US)
            }
        }
    }

    protected fun getMonthFormat(dateFormat: String?): SimpleDateFormat {
        return if (this::monthSDF.isInitialized) {
            monthSDF
        } else {
            try {
                val monthFormat = when {
                    dateFormat?.contains("MMMM") == true -> "MMMM"
                    dateFormat?.contains("MMM") == true -> "MMM"
                    dateFormat?.contains("MM") == true -> "MM"
                    dateFormat?.contains("M") == true -> "M"
                    else -> DEFAULT_MONTH_FORMAT
                }
                SimpleDateFormat(monthFormat, Locale.US)
            } catch (e: Exception) {
                logException(e)
                SimpleDateFormat(DEFAULT_MONTH_FORMAT, Locale.US)
            }
        }
    }

    protected fun getYearFormat(dateFormat: String?): SimpleDateFormat {
        return if (this::yearSDF.isInitialized) {
            yearSDF
        } else {
            try {
                val yearFormat = when {
                    dateFormat?.contains("yyyy") == true -> "yyyy"
                    dateFormat?.contains("yy") == true -> "yy"
                    else -> DEFAULT_YEAR_FORMAT
                }
                SimpleDateFormat(yearFormat, Locale.US)
            } catch (e: Exception) {
                logException(e)
                SimpleDateFormat(DEFAULT_YEAR_FORMAT, Locale.US)
            }
        }
    }

    companion object {
        private const val DEFAULT_DAY_FORMAT = "dd"
        private const val DEFAULT_MONTH_FORMAT = "MM"
        private const val DEFAULT_YEAR_FORMAT = "yyyy"
    }
}