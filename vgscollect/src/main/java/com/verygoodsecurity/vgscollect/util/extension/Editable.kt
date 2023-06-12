package com.verygoodsecurity.vgscollect.util.extension

import android.text.Editable

internal fun Editable.replaceIgnoreFilters(start: Int, end: Int, text: CharSequence): Editable? {
    val f = filters
    filters = arrayOf()
    val result = replace(start, end, text)
    result?.filters = f
    return result
}