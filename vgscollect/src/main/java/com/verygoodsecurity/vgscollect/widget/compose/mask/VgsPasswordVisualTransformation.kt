package com.verygoodsecurity.vgscollect.widget.compose.mask

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText

private const val DEFAULT_PASSWORD_MASK_CHAR = '\u2022'

class VgsPasswordVisualTransformation(
    val passwordChar: Char = DEFAULT_PASSWORD_MASK_CHAR
) : VgsVisualTransformation() {

    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            AnnotatedString(passwordChar.toString().repeat(text.text.length)),
            OffsetMapping.Identity
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VgsPasswordVisualTransformation) return false
        if (passwordChar != other.passwordChar) return false
        return true
    }

    override fun hashCode(): Int {
        return passwordChar.hashCode()
    }
}