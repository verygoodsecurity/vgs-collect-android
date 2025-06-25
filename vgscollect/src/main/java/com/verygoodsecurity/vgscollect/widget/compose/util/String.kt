package com.verygoodsecurity.vgscollect.widget.compose.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal const val MASK_SYMBOL = '#'

internal fun String.format(mask: String): String {
    val maskSymbolsCount = mask.count { it == MASK_SYMBOL }
    var out = ""
    var maskIndex = 0
    this.forEachIndexed { index, char ->
        if (index >= maskSymbolsCount) return out
        while (mask.getOrNull(maskIndex) != MASK_SYMBOL) {
            out += mask[maskIndex]
            maskIndex++
        }
        out += char
        maskIndex++
    }
    return out
}

internal fun String.parse(format: String): Date? {
    return try {
        SimpleDateFormat(format, Locale.US).parse(this)
    } catch (_: Exception) {
        null
    }
}