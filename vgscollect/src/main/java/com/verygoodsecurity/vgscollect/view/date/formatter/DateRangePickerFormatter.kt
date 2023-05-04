package com.verygoodsecurity.vgscollect.view.date.formatter

import com.verygoodsecurity.vgscollect.view.date.DatePickerMode

internal interface DateRangePickerFormatter {
    var dateFormatter: VGSDateFormat
    var pickerMode: DatePickerMode
}
