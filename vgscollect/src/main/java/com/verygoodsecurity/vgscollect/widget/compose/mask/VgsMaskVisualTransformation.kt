package com.verygoodsecurity.vgscollect.widget.compose.mask

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TransformedText

/**
 * Visual transformation that formats input against a fixed character mask.
 *
 * Each `#` in [mask] is a placeholder for one input character; any other
 * character (space, slash, dash, …) is rendered as-is. For example
 * `"#### #### #### ####"` shows card-number input grouped into four blocks.
 *
 * The mask is purely visual — the value submitted to VGS is the raw, unformatted text.
 *
 * @param mask display pattern; use `#` for input characters and any other character as a literal.
 */
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