package com.verygoodsecurity.vgscollect.util.extension

import java.util.*

internal fun Calendar.setMaximumTime() {
    this.apply {
        set(Calendar.HOUR_OF_DAY, getActualMaximum(Calendar.HOUR_OF_DAY))
        set(Calendar.MINUTE, getActualMaximum(Calendar.MINUTE))
        set(Calendar.SECOND, getActualMaximum(Calendar.SECOND))
        set(Calendar.MILLISECOND, getActualMaximum(Calendar.MILLISECOND))
    }
}