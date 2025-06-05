package com.verygoodsecurity.vgscollect.widget.compose.mask

internal class MaskFormatter(
    val mask: String,
    val maskSymbol: Char,
    val text: String
) {

    fun getFormatterText(): String {
        val maskSymbolsCount = mask.count { it == maskSymbol }
        var out = ""
        var maskIndex = 0
        text.forEachIndexed { index, char ->
            if (index >= maskSymbolsCount) return out
            while (mask.getOrNull(maskIndex) != maskSymbol) {
                out += mask[maskIndex]
                maskIndex++
            }
            out += char
            maskIndex++
        }
        return out
    }

    fun getOriginalToTransformedOffset(offset: Int): Int {
        if (offset == 0) return 0
        var numberOfMaskSymbols = 0
        val masked = mask.takeWhile {
            if (it == maskSymbol) numberOfMaskSymbols++
            numberOfMaskSymbols < offset
        }
        return (masked.length + 1).coerceIn(0, mask.length)
    }

    fun getTransformedToOriginalOffset(offset: Int): Int {
        return mask.take(offset).count { it == maskSymbol }
    }
}