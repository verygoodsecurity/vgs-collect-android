package com.verygoodsecurity.vgscollect.widget.compose.mask

import androidx.compose.ui.text.input.OffsetMapping

internal class MaskOffsetMapping(val formatter: MaskFormatter) : OffsetMapping {

    override fun originalToTransformed(offset: Int): Int {
        return formatter.getOriginalToTransformedOffset(offset)
    }

    override fun transformedToOriginal(offset: Int): Int {
        return formatter.getTransformedToOriginalOffset(offset)
    }
}