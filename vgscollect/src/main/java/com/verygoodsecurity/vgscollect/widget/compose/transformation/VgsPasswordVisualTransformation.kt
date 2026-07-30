package com.verygoodsecurity.vgscollect.widget.compose.transformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText

private const val DEFAULT_PASSWORD_MASK_CHAR = '\u2022'

/**
 * Visual transformation that hides each input character behind a mask glyph.
 *
 * Use it on sensitive fields where the value should not be visible while typing.
 * The mask is purely visual — the value submitted to VGS is the raw input.
 *
 * @param passwordChar character drawn in place of each input character. Defaults to the bullet `•`.
 * @param range index range of characters to mask with [passwordChar].
 *  For example, `range = 0..11` on input `"4111111111111111"` displays `"••••••••••••1111"`.
 */
class VgsPasswordVisualTransformation(
    val passwordChar: Char = DEFAULT_PASSWORD_MASK_CHAR,
    val range: IntRange = IntRange(0, Int.MAX_VALUE)
) : VgsVisualTransformation() {

    override fun filter(text: AnnotatedString): TransformedText {
        val result = text.foldIndexed(StringBuilder()) { index, acc, unit ->
            if (index in range) acc.append(passwordChar) else acc.append(unit)
        }.toString()
        return TransformedText(
            AnnotatedString(result),
            OffsetMapping.Identity
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VgsPasswordVisualTransformation) return false
        if (passwordChar != other.passwordChar) return false
        if (range != other.range) return false
        return true
    }

    override fun hashCode(): Int {
        var result = passwordChar.hashCode()
        result = 31 * result + range.hashCode()
        return result
    }
}