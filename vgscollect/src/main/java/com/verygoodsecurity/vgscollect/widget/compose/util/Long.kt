package com.verygoodsecurity.vgscollect.widget.compose.util

import java.util.Calendar

internal fun Long.plusYears(years: Int): Long {
    val calendar = Calendar.getInstance().apply { timeInMillis = this@plusYears }
    calendar.add(Calendar.YEAR, years)
    return calendar.timeInMillis
}