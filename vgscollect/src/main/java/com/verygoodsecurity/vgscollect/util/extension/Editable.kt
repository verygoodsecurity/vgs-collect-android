package com.verygoodsecurity.vgscollect.util.extension

import android.text.Editable

internal fun Editable.replaceIgnoreFilters(start: Int, end: Int, text: CharSequence): Editable? {
    val rememberFilters = filters
    filters = arrayOf()
    val result = replace(start, end, text)
    result?.filters = rememberFilters
    return result
}