package com.verygoodsecurity.vgscollect.widget.compose.transformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import com.verygoodsecurity.vgscollect.widget.compose.util.MASK_SYMBOL
import com.verygoodsecurity.vgscollect.widget.compose.util.format

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
        val formatted = maskFormatter.getFormatterText()
        val carriedSpans = text.spanStyles.mapNotNull { range ->
            val start = range.start.coerceIn(0, formatted.length)
            val end = range.end.coerceIn(start, formatted.length)
            if (start < end) AnnotatedString.Range(range.item, start, end) else null
        }
        return TransformedText(
            AnnotatedString(formatted, carriedSpans),
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

    internal class MaskOffsetMapping(val formatter: MaskFormatter) : OffsetMapping {

        override fun originalToTransformed(offset: Int): Int {
            return formatter.getOriginalToTransformedOffset(offset)
        }

        override fun transformedToOriginal(offset: Int): Int {
            return formatter.getTransformedToOriginalOffset(offset)
        }
    }

    internal class MaskFormatter(val mask: String, val text: String) {

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
}