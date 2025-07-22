package com.verygoodsecurity.vgscollect.widget.compose.date

import com.verygoodsecurity.vgscollect.widget.compose.util.format
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class VgsExpiryDateFormat(val dateFormat: String) {

    val mask: String = dateFormat.replace('m', '#', true).replace('y', '#', true)

    val maskChartsCount: Int = mask.count { it == '#' }

    fun parse(text: String): Date? {
        val formatted = text.format(mask)
        return try {
            SimpleDateFormat(dateFormat, Locale.US).parse(formatted)
        } catch (_: Exception) {
            null
        }
    }

    fun format(date: Date?): String? {
        return try {
            date?.let { SimpleDateFormat(dateFormat, Locale.US).format(date) }
        } catch (_: Exception) {
            null
        }
    }

    class MonthShortYear : VgsExpiryDateFormat(dateFormat = "MM/yy")

    class MonthLongYear : VgsExpiryDateFormat(dateFormat = "MM/yyyy")

    class ShortYearMonth : VgsExpiryDateFormat(dateFormat = "yy/MM")

    class LongYearMonth : VgsExpiryDateFormat(dateFormat = "yyyy/MM")
}