package com.verygoodsecurity.vgscollect.widget.compose.mask

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import com.verygoodsecurity.vgscollect.widget.compose.transformation.VgsMaskVisualTransformation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VgsMaskVisualTransformationTest {

    private val ssnTransformation = VgsMaskVisualTransformation("###-##-####")

    // filter — transformed text

    @Test
    fun filter_fullSsnInput_formattedTextReturned() {
        val result = ssnTransformation.filter(AnnotatedString("123456789"))
        assertEquals("123-45-6789", result.text.text)
    }

    @Test
    fun filter_partialSsnInput_partiallyFormattedTextReturned() {
        val result = ssnTransformation.filter(AnnotatedString("123"))
        assertEquals("123", result.text.text)
    }

    @Test
    fun filter_partialInputCrossingSeparator_separatorIncluded() {
        val result = ssnTransformation.filter(AnnotatedString("1234"))
        assertEquals("123-4", result.text.text)
    }

    @Test
    fun filter_emptyInput_emptyTextReturned() {
        val result = ssnTransformation.filter(AnnotatedString(""))
        assertEquals("", result.text.text)
    }

    // filter — offset mapping

    @Test
    fun filter_offsetMapping_originalToTransformed_separatorSkipped() {
        val result = ssnTransformation.filter(AnnotatedString("123456789"))
        // original offset 4 → transformed "123-4" → position 5
        assertEquals(5, result.offsetMapping.originalToTransformed(4))
    }

    @Test
    fun filter_offsetMapping_transformedToOriginal_separatorNotCounted() {
        val result = ssnTransformation.filter(AnnotatedString("123456789"))
        // transformed offset 4 is at '-' → original 3
        assertEquals(3, result.offsetMapping.transformedToOriginal(4))
    }

    // filter — span propagation

    @Test
    fun filter_textWithSpanStyle_notSpanningSeparator_carriedOverUnchanged() {
        val styled = AnnotatedString(
            "123456789",
            spanStyles = listOf(AnnotatedString.Range(SpanStyle(color = Color.Red), 0, 3))
        )
        val result = ssnTransformation.filter(styled)
        assertEquals("123-45-6789", result.text.text)
        // span ends exactly where the separator is inserted, so unshifted indices are exact
        assertEquals(
            listOf(AnnotatedString.Range(SpanStyle(color = Color.Red), 0, 3)),
            result.text.spanStyles
        )
    }

    @Test
    fun filter_textWithSpanStyle_spanningSeparator_carriedOverAtUnshiftedIndices() {
        val styled = AnnotatedString(
            "123456789",
            spanStyles = listOf(AnnotatedString.Range(SpanStyle(color = Color.Red), 0, 4))
        )
        val result = ssnTransformation.filter(styled)
        assertEquals("123-45-6789", result.text.text)
        // approximate: carried at the original 0..4, not repositioned past the inserted
        // '-', so it ends up covering "123-" instead of "123-4"
        assertEquals(
            listOf(AnnotatedString.Range(SpanStyle(color = Color.Red), 0, 4)),
            result.text.spanStyles
        )
    }

    @Test
    fun filter_spanBeyondFormattedLength_clampedToFormattedLength() {
        val styled = AnnotatedString(
            "123",
            spanStyles = listOf(AnnotatedString.Range(SpanStyle(color = Color.Red), 0, 9))
        )
        val result = ssnTransformation.filter(styled)
        assertEquals("123", result.text.text)
        assertEquals(
            listOf(AnnotatedString.Range(SpanStyle(color = Color.Red), 0, 3)),
            result.text.spanStyles
        )
    }

    // equals / hashCode

    @Test
    fun equals_sameMask_trueReturned() {
        assertTrue(VgsMaskVisualTransformation("###-##-####") == VgsMaskVisualTransformation("###-##-####"))
    }

    @Test
    fun equals_differentMask_falseReturned() {
        assertFalse(VgsMaskVisualTransformation("###-##-####") == VgsMaskVisualTransformation("#### #### #### ####"))
    }

    @Test
    fun hashCode_sameMask_sameHashCodeReturned() {
        assertEquals(
            VgsMaskVisualTransformation("###-##-####").hashCode(),
            VgsMaskVisualTransformation("###-##-####").hashCode()
        )
    }
}
