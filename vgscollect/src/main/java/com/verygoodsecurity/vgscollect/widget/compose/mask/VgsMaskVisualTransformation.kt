package com.verygoodsecurity.vgscollect.widget.compose.mask

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TransformedText

class VgsMaskVisualTransformation(val mask: String) : VgsVisualTransformation() {

    override fun filter(text: AnnotatedString): TransformedText {
        val maskFormatter = MaskFormatter(mask, text.text)
        return TransformedText(
            AnnotatedString(maskFormatter.getFormatterText()),
            MaskOffsetMapping(maskFormatter)
        )
    }
}