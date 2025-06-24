package com.verygoodsecurity.vgscollect.widget.compose.mask

import com.verygoodsecurity.vgscollect.widget.compose.util.MASK_SYMBOL
import com.verygoodsecurity.vgscollect.widget.compose.util.format

internal class MaskFormatter(
    val mask: String,
    val text: String
) {

    fun getFormatterText(): String {
        return text.format(mask)
    }

    fun getOriginalToTransformedOffset(offset: Int): Int {
        if (offset == 0) return 0
        var numberOfMaskSymbols = 0
        val masked = mask.takeWhile {
            if (it == MASK_SYMBOL) numberOfMaskSymbols++
            numberOfMaskSymbols < offset
        }
        return (masked.length + 1).coerceIn(0, mask.length)
    }

    fun getTransformedToOriginalOffset(offset: Int): Int {
        return mask.take(offset).count { it == MASK_SYMBOL }
    }
}