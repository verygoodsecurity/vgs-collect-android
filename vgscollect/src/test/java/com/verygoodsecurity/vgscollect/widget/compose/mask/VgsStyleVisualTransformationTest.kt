package com.verygoodsecurity.vgscollect.widget.compose.mask

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import com.verygoodsecurity.vgscollect.widget.compose.transformation.VgsChainedVisualTransformation
import com.verygoodsecurity.vgscollect.widget.compose.transformation.VgsMaskVisualTransformation
import com.verygoodsecurity.vgscollect.widget.compose.transformation.VgsPasswordVisualTransformation
import com.verygoodsecurity.vgscollect.widget.compose.transformation.VgsStyleVisualTransformation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VgsStyleVisualTransformationTest {

    private val redStyle = SpanStyle(color = Color.Red)
    private val blueStyle = SpanStyle(color = Color.Blue)

    // filter - ranges

    @Test
    fun filter_range_textUnchanged() {
        val transformation = VgsStyleVisualTransformation(
            listOf(AnnotatedString.Range(redStyle, 0, 3))
        )
        val result = transformation.filter(AnnotatedString("hello"))
        assertEquals("hello", result.text.text)
    }

    @Test
    fun filter_range_styleApplied() {
        val transformation = VgsStyleVisualTransformation(
            listOf(AnnotatedString.Range(redStyle, 0, 3))
        )
        val result = transformation.filter(AnnotatedString("hello"))
        assertEquals(listOf(AnnotatedString.Range(redStyle, 0, 3)), result.text.spanStyles)
    }

    @Test
    fun filter_multipleRanges_allStylesApplied() {
        val transformation = VgsStyleVisualTransformation(
            listOf(
                AnnotatedString.Range(redStyle, 0, 2),
                AnnotatedString.Range(blueStyle, 2, 5)
            )
        )
        val result = transformation.filter(AnnotatedString("hello"))
        assertEquals(
            listOf(
                AnnotatedString.Range(redStyle, 0, 2),
                AnnotatedString.Range(blueStyle, 2, 5)
            ),
            result.text.spanStyles
        )
    }

    // filter - bounds safety

    @Test
    fun filter_rangeBeyondTextLength_clampedToLength() {
        val transformation = VgsStyleVisualTransformation(
            listOf(AnnotatedString.Range(redStyle, 0, 100))
        )
        val result = transformation.filter(AnnotatedString("ab"))
        assertEquals(listOf(AnnotatedString.Range(redStyle, 0, 2)), result.text.spanStyles)
    }

    @Test
    fun filter_rangeStartBeyondTextLength_rangeDropped() {
        val transformation = VgsStyleVisualTransformation(
            listOf(AnnotatedString.Range(redStyle, 10, 20))
        )
        val result = transformation.filter(AnnotatedString("ab"))
        assertTrue(result.text.spanStyles.isEmpty())
    }

    @Test
    fun filter_emptyText_noStylesApplied() {
        val transformation = VgsStyleVisualTransformation(
            listOf(AnnotatedString.Range(redStyle, 0, 5))
        )
        val result = transformation.filter(AnnotatedString(""))
        assertEquals("", result.text.text)
        assertTrue(result.text.spanStyles.isEmpty())
    }

    // filter - offset mapping

    @Test
    fun filter_offsetMapping_identityMappingApplied() {
        val transformation = VgsStyleVisualTransformation(
            listOf(AnnotatedString.Range(redStyle, 0, 3))
        )
        val result = transformation.filter(AnnotatedString("hello"))
        assertEquals(3, result.offsetMapping.originalToTransformed(3))
        assertEquals(3, result.offsetMapping.transformedToOriginal(3))
    }

    // chaining

    @Test
    fun filter_chainedAfterMask_styleAppliedToMaskedText() {
        val transformation = VgsChainedVisualTransformation(
            listOf(
                VgsMaskVisualTransformation("##-##"),
                VgsStyleVisualTransformation(listOf(AnnotatedString.Range(redStyle, 0, 2)))
            )
        )
        val result = transformation.filter(AnnotatedString("1234"))
        assertEquals("12-34", result.text.text)
        assertEquals(listOf(AnnotatedString.Range(redStyle, 0, 2)), result.text.spanStyles)
    }

    @Test
    fun filter_chainedBeforeMask_styleCarriedAtUnshiftedIndices() {
        // Style survives being placed before Mask — Mask carries forward styling
        // applied by an earlier step — but at unchanged indices, not repositioned for
        // the separator Mask inserts. Here the range doesn't cross the separator, so
        // the unshifted indices are still exact.
        val transformation = VgsChainedVisualTransformation(
            listOf(
                VgsStyleVisualTransformation(listOf(AnnotatedString.Range(redStyle, 0, 2))),
                VgsMaskVisualTransformation("##-##")
            )
        )
        val result = transformation.filter(AnnotatedString("1234"))
        assertEquals("12-34", result.text.text)
        assertEquals(listOf(AnnotatedString.Range(redStyle, 0, 2)), result.text.spanStyles)
    }

    @Test
    fun filter_chainedBeforeMask_styleSpanningSeparator_approximatedShort() {
        val transformation = VgsChainedVisualTransformation(
            listOf(
                VgsStyleVisualTransformation(listOf(AnnotatedString.Range(redStyle, 0, 3))),
                VgsMaskVisualTransformation("##-##")
            )
        )
        val result = transformation.filter(AnnotatedString("1234"))
        assertEquals("12-34", result.text.text)
        // raw 0..3 ("123") crosses the inserted '-' but isn't repositioned for it —
        // ends up covering "12-" instead of "12-3"
        assertEquals(listOf(AnnotatedString.Range(redStyle, 0, 3)), result.text.spanStyles)
    }

    @Test
    fun filter_chainedWithPasswordAndMask_styleAppliedToMaskedFormattedText() {
        val transformation = VgsChainedVisualTransformation(
            listOf(
                VgsPasswordVisualTransformation(),
                VgsMaskVisualTransformation("##-##"),
                VgsStyleVisualTransformation(listOf(AnnotatedString.Range(redStyle, 0, 2)))
            )
        )
        val result = transformation.filter(AnnotatedString("1234"))
        assertEquals("••-••", result.text.text)
        assertEquals(listOf(AnnotatedString.Range(redStyle, 0, 2)), result.text.spanStyles)
    }

    // equals / hashCode

    @Test
    fun equals_sameRanges_trueReturned() {
        val ranges = listOf(AnnotatedString.Range(redStyle, 0, 3))
        assertTrue(VgsStyleVisualTransformation(ranges) == VgsStyleVisualTransformation(ranges))
    }

    @Test
    fun equals_differentRanges_falseReturned() {
        assertFalse(
            VgsStyleVisualTransformation(listOf(AnnotatedString.Range(redStyle, 0, 3))) ==
                VgsStyleVisualTransformation(listOf(AnnotatedString.Range(blueStyle, 0, 3)))
        )
    }

    @Test
    fun hashCode_sameRanges_sameHashCodeReturned() {
        val ranges = listOf(AnnotatedString.Range(redStyle, 0, 3))
        assertEquals(
            VgsStyleVisualTransformation(ranges).hashCode(),
            VgsStyleVisualTransformation(ranges).hashCode()
        )
    }
}
