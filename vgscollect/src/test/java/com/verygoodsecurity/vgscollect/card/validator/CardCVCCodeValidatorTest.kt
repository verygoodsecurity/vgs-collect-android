package com.verygoodsecurity.vgscollect.card.validator

import com.verygoodsecurity.vgscollect.view.card.validation.CardCVCCodeValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CardCVCCodeValidatorTest {
    private val cvcValidator = CardCVCCodeValidator()

    @Test
    fun notValidCVC_symbol() {
        assertFalse(cvcValidator.isValid("12d3"))
    }

    @Test
    fun notValidCVC_less3() {
        assertFalse(cvcValidator.isValid("12"))
    }

    @Test
    fun valid3CVC() {
        assertTrue(cvcValidator.isValid("123"))
    }

    @Test
    fun valid4CVC() {
        assertTrue(cvcValidator.isValid("1275"))
    }
}