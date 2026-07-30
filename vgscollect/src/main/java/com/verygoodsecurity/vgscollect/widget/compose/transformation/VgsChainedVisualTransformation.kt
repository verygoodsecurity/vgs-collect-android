package com.verygoodsecurity.vgscollect.widget.compose.transformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText

/**
 * Visual transformation that applies multiple [VgsVisualTransformation]s in sequence.
 *
 * Each transformation receives the output of the previous one, and cursor offset
 * mappings are composed automatically so that cursor positioning remains correct
 * across the entire chain.
 *
 * @param transformations ordered list of transformations to apply. Order matters —
 *   the first transformation is applied first, and each subsequent one transforms
 *   the result of the previous.
 */
class VgsChainedVisualTransformation(val transformations: List<VgsVisualTransformation>) :
    VgsVisualTransformation() {

    override fun filter(text: AnnotatedString): TransformedText {
        val result = transformations.scan(
            TransformedText(
                text,
                OffsetMapping.Identity
            )
        ) { value, transformation ->
            transformation.filter(value.text)
        }
        return TransformedText(
            result.last().text,
            ChainedOffsetMapping(result.map { it.offsetMapping })
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VgsChainedVisualTransformation) return false
        if (transformations != other.transformations) return false
        return true
    }

    override fun hashCode(): Int {
        return transformations.hashCode()
    }

    internal class ChainedOffsetMapping(val offsetMappings: List<OffsetMapping>) : OffsetMapping {

        override fun originalToTransformed(offset: Int): Int {
            return offsetMappings.fold(offset) { acc, mapping ->
                mapping.originalToTransformed(acc)
            }
        }

        override fun transformedToOriginal(offset: Int): Int {
            return offsetMappings.foldRight(offset) { mapping, acc ->
                mapping.transformedToOriginal(acc)
            }
        }
    }
}