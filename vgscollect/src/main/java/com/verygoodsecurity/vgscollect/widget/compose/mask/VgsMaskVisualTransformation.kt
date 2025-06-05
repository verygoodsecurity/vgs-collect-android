package com.verygoodsecurity.vgscollect.widget.compose.mask

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TransformedText

class VgsMaskVisualTransformation(
    val mask: String,
    val maskSymbol: Char
) : VgsVisualTransformation() {

    override fun filter(text: AnnotatedString): TransformedText {
        val maskFormatter = MaskFormatter(mask, maskSymbol, text.text)
        return TransformedText(
            AnnotatedString(maskFormatter.getFormatterText()),
            MaskOffsetMapping(maskFormatter)
        )
    }
}