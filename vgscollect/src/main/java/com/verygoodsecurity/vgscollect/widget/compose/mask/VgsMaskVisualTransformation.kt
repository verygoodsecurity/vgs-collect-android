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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VgsMaskVisualTransformation) return false
        if (mask != other.mask) return false
        return true
    }

    override fun hashCode(): Int {
        return mask.hashCode()
    }
}