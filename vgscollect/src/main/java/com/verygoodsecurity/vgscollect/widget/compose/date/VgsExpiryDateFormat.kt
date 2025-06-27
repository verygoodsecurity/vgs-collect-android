package com.verygoodsecurity.vgscollect.widget.compose.date

sealed class VgsExpiryDateFormat(val dateFormat: String) {

    val mask: String = dateFormat
        .replace('m', '#', true)
        .replace('y', '#', true)

    val maskChartsCount: Int = mask.count { it == '#' }

    class MonthShortYear : VgsExpiryDateFormat(dateFormat = "MM/yy")

    class MonthLongYear : VgsExpiryDateFormat(dateFormat = "MM/yyyy")

    class ShortYearMonth : VgsExpiryDateFormat(dateFormat = "yy/MM")

    class LongYearMonth : VgsExpiryDateFormat(dateFormat = "yyyy/MM")
}