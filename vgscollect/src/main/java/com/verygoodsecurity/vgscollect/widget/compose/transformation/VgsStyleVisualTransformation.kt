package com.verygoodsecurity.vgscollect.widget.compose.transformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText

/**
 * Visual transformation that applies [SpanStyle]s (color, weight, letter spacing, etc.)
 * to fixed ranges of the displayed text, without ever exposing the text itself.
 *
 * [styles] only ever describes *positions* — it never has access to the field's
 * characters — so it can be used on any field, masked or not, without creating a
 * way to read back sensitive input. Ranges are relative to whatever text this
 * transformation receives: the raw field value when used on its own, or the
 * already-transformed text of an earlier step when composed via
 * [VgsChainedVisualTransformation] (e.g. after [VgsMaskVisualTransformation]).
 *
 * Ranges outside the current text length are clamped, so a rule written for the
 * field's expected final length is safe to use while the value is still shorter
 * (e.g. mid-typing).
 *
 * Placed earlier in a [VgsChainedVisualTransformation], the styles applied here still
 * reach the final output — [VgsPasswordVisualTransformation] and
 * [VgsMaskVisualTransformation] both carry forward whatever's already on their input —
 * but at unchanged indices, not repositioned for what a later step inserts. Chain this
 * *after* both for exact positions; placed before [VgsMaskVisualTransformation], a range
 * that spans one of its separators lands a little short of the intended characters.
 *
 * @param styles style ranges to apply to the displayed text.
 */
class VgsStyleVisualTransformation(
    private val styles: List<AnnotatedString.Range<SpanStyle>>
) : VgsVisualTransformation() {

    override fun filter(text: AnnotatedString): TransformedText {
        val length = text.text.length
        val builder = AnnotatedString.Builder(text)
        styles.forEach { range ->
            val start = range.start.coerceIn(0, length)
            val end = range.end.coerceIn(start, length)
            if (start < end) {
                builder.addStyle(range.item, start, end)
            }
        }
        return TransformedText(builder.toAnnotatedString(), OffsetMapping.Identity)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VgsStyleVisualTransformation) return false
        return styles == other.styles
    }

    override fun hashCode(): Int {
        return styles.hashCode()
    }
}
