package com.verygoodsecurity.vgscollect.widget.compose.mask

import org.junit.Assert.assertEquals
import org.junit.Test

class MaskFormatterTest {

    // getFormatterText

    @Test
    fun getFormatterText_ssnMask_formattedSuccessfully() {
        assertEquals("123-45-6789", MaskFormatter("###-##-####", "123456789").getFormatterText())
    }

    @Test
    fun getFormatterText_cardNumberMask_formattedSuccessfully() {
        assertEquals(
            "1234 5678 9012 3456",
            MaskFormatter("#### #### #### ####", "1234567890123456").getFormatterText()
        )
    }

    @Test
    fun getFormatterText_expiryMask_formattedSuccessfully() {
        assertEquals("12/25", MaskFormatter("##/##", "1225").getFormatterText())
    }

    @Test
    fun getFormatterText_partialInput_partiallyFormatted() {
        assertEquals("123", MaskFormatter("###-##-####", "123").getFormatterText())
    }

    @Test
    fun getFormatterText_partialInputCrossingSeparator_separatorIncluded() {
        assertEquals("123-4", MaskFormatter("###-##-####", "1234").getFormatterText())
    }

    @Test
    fun getFormatterText_emptyInput_emptyStringReturned() {
        assertEquals("", MaskFormatter("###-##-####", "").getFormatterText())
    }

    @Test
    fun getFormatterText_inputLongerThanMask_truncatedToMaskSymbolCount() {
        // mask has 9 '#' symbols; 10th digit is dropped
        assertEquals("123-45-6789", MaskFormatter("###-##-####", "1234567890").getFormatterText())
    }

    // getOriginalToTransformedOffset

    @Test
    fun getOriginalToTransformedOffset_zero_zeroReturned() {
        assertEquals(0, MaskFormatter("###-##-####", "123456789").getOriginalToTransformedOffset(0))
    }

    @Test
    fun getOriginalToTransformedOffset_beforeFirstSeparator_noShiftApplied() {
        val formatter = MaskFormatter("###-##-####", "123456789")
        assertEquals(1, formatter.getOriginalToTransformedOffset(1))
        assertEquals(2, formatter.getOriginalToTransformedOffset(2))
        assertEquals(3, formatter.getOriginalToTransformedOffset(3))
    }

    @Test
    fun getOriginalToTransformedOffset_afterFirstSeparator_separatorCountedInOffset() {
        // original offset 4 → masked "123-4" → transformed offset 5
        assertEquals(5, MaskFormatter("###-##-####", "123456789").getOriginalToTransformedOffset(4))
    }

    @Test
    fun getOriginalToTransformedOffset_afterSecondSeparator_bothSeparatorsCountedInOffset() {
        // original offset 6 → masked "123-45-6" → transformed offset 8
        assertEquals(8, MaskFormatter("###-##-####", "123456789").getOriginalToTransformedOffset(6))
    }

    // getTransformedToOriginalOffset

    @Test
    fun getTransformedToOriginalOffset_zero_zeroReturned() {
        assertEquals(0, MaskFormatter("###-##-####", "123456789").getTransformedToOriginalOffset(0))
    }

    @Test
    fun getTransformedToOriginalOffset_beforeSeparator_maskSymbolCountReturned() {
        val formatter = MaskFormatter("###-##-####", "123456789")
        assertEquals(1, formatter.getTransformedToOriginalOffset(1))
        assertEquals(2, formatter.getTransformedToOriginalOffset(2))
        assertEquals(3, formatter.getTransformedToOriginalOffset(3))
    }

    @Test
    fun getTransformedToOriginalOffset_atSeparator_sameAsBeforeSeparator() {
        // transformed offset 4 is at the '-'; still maps to original 3
        assertEquals(3, MaskFormatter("###-##-####", "123456789").getTransformedToOriginalOffset(4))
    }

    @Test
    fun getTransformedToOriginalOffset_afterSeparator_separatorNotCounted() {
        // transformed offset 5 → "###-#" → 4 mask symbols → original 4
        assertEquals(4, MaskFormatter("###-##-####", "123456789").getTransformedToOriginalOffset(5))
    }
}
