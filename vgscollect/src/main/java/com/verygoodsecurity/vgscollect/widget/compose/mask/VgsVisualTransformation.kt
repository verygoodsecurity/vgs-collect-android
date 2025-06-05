package com.verygoodsecurity.vgscollect.widget.compose.mask

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

abstract class VgsVisualTransformation internal constructor() : VisualTransformation {

    companion object {

        val None: VgsVisualTransformation = object : VgsVisualTransformation() {

            override fun filter(text: AnnotatedString): TransformedText {
                return TransformedText(text, OffsetMapping.Identity)
            }
        }
    }
}
