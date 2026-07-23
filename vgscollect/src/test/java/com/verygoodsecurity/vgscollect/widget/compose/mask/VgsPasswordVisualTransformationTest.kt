package com.verygoodsecurity.vgscollect.widget.compose.mask

import androidx.compose.ui.text.AnnotatedString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VgsPasswordVisualTransformationTest {

    private val transformation = VgsPasswordVisualTransformation()

    // filter

    @Test
    fun filter_text_allCharsReplacedWithBullet() {
        val result = transformation.filter(AnnotatedString("hello"))
        assertEquals("•••••", result.text.text)
    }

    @Test
    fun filter_text_lengthPreserved() {
        val input = "abc123"
        val result = transformation.filter(AnnotatedString(input))
        assertEquals(input.length, result.text.text.length)
    }

    @Test
    fun filter_emptyText_emptyStringReturned() {
        val result = transformation.filter(AnnotatedString(""))
        assertEquals("", result.text.text)
    }

    @Test
    fun filter_customPasswordChar_allCharsReplacedWithCustomChar() {
        val result = VgsPasswordVisualTransformation('*').filter(AnnotatedString("hello"))
        assertEquals("*****", result.text.text)
    }

    @Test
    fun filter_offsetMapping_identityMappingApplied() {
        val result = transformation.filter(AnnotatedString("hello"))
        assertEquals(3, result.offsetMapping.originalToTransformed(3))
        assertEquals(3, result.offsetMapping.transformedToOriginal(3))
    }

    // equals / hashCode

    @Test
    fun equals_samePasswordChar_trueReturned() {
        assertTrue(VgsPasswordVisualTransformation('*') == VgsPasswordVisualTransformation('*'))
    }

    @Test
    fun equals_differentPasswordChar_falseReturned() {
        assertFalse(VgsPasswordVisualTransformation('*') == VgsPasswordVisualTransformation('#'))
    }

    @Test
    fun hashCode_samePasswordChar_sameHashCodeReturned() {
        assertEquals(
            VgsPasswordVisualTransformation('*').hashCode(),
            VgsPasswordVisualTransformation('*').hashCode()
        )
    }
}
