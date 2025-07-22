package com.verygoodsecurity.vgscollect.widget.compose.util

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