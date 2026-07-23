package com.verygoodsecurity.vgscollect.widget.compose.mask

import androidx.compose.ui.text.AnnotatedString
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
