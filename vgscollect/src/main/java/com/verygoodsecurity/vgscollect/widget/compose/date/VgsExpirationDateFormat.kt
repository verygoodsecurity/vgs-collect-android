package com.verygoodsecurity.vgscollect.widget.compose.date

sealed class VgsExpirationDateFormat(
    val format: String,
    val mask: String
) {

    class MonthShortYear : VgsExpirationDateFormat(
        format = "MM/yy",
        mask = "##/##"
    )

    class MonthLongYear : VgsExpirationDateFormat(
        format = "MM/yyyy",
        mask = "##/####"
    )

    class ShortYearMonth : VgsExpirationDateFormat(
        format = "yy/MM",
        mask = "##/##"
    )

    class LongYearMonth : VgsExpirationDateFormat(
        format = "yyyy/MM",
        mask = "####/##"
    )
}