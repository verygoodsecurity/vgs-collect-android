package com.verygoodsecurity.vgscollect.view.card.formatter.date

import com.verygoodsecurity.vgscollect.view.card.formatter.Formatter
import com.verygoodsecurity.vgscollect.view.date.DatePickerMode

interface DatePickerFormatter:
    Formatter {
    fun setMode(mode: DatePickerMode)
}