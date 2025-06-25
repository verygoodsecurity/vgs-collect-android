package com.verygoodsecurity.vgscollect.widget.compose.date

private const val SHORT_YEAR_INPUT_LENGTH = 4
private const val LONG_YEAR_INPUT_LENGTH = 6

sealed class VgsExpirationDateFormat(
    val dateFormat: String,
    val mask: String,
    val inputLength: Int,
) {

    class MonthShortYear : VgsExpirationDateFormat(
        dateFormat = "MM/yy",
        mask = "##/##",
        inputLength = SHORT_YEAR_INPUT_LENGTH
    )

    class MonthLongYear : VgsExpirationDateFormat(
        dateFormat = "MM/yyyy",
        mask = "##/####",
        inputLength = LONG_YEAR_INPUT_LENGTH
    )

    class ShortYearMonth : VgsExpirationDateFormat(
        dateFormat = "yy/MM",
        mask = "##/##",
        inputLength = SHORT_YEAR_INPUT_LENGTH
    )

    class LongYearMonth : VgsExpirationDateFormat(
        dateFormat = "yyyy/MM",
        mask = "####/##",
        inputLength = LONG_YEAR_INPUT_LENGTH
    )
}