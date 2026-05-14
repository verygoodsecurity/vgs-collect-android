package com.verygoodsecurity.vgscollect.widget.compose.mask

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Compose [VisualTransformation] type accepted by VGS Compose text fields.
 *
 * Pass one of the built-in implementations to a field's `visualTransformation`
 * parameter to change how the value is displayed without affecting the value
 * submitted to the VGS vault. Built-in options:
 * [VgsMaskVisualTransformation] (formatted display, e.g. `#### #### #### ####`),
 * [VgsPasswordVisualTransformation] (mask each character), and [None] (no change).
 */
abstract class VgsVisualTransformation internal constructor() : VisualTransformation {

    companion object {

        /** No visual transformation; the value is displayed as typed. */
        val None: VgsVisualTransformation = object : VgsVisualTransformation() {

            override fun filter(text: AnnotatedString): TransformedText {
                return TransformedText(text, OffsetMapping.Identity)
            }
        }
    }
}
