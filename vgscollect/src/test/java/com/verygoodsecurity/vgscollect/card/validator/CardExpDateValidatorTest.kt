package com.verygoodsecurity.vgscollect.card.validator

import com.verygoodsecurity.vgscollect.view.card.validation.CardExpDateValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CardExpDateValidatorTest {
    private val validator = CardExpDateValidator()

    @Test
    fun date_text() {
        assertFalse(validator.isValid("12 of June"))
    }

    @Test
    fun date_wrong_number() {
        assertFalse(validator.isValid("33/34"))
    }

    @Test
    fun date_number() {
        assertTrue(validator.isValid("06/25"))
    }

    @Test
    fun date_number_full_year() {
        assertTrue(validator.isValid("06/2024"))
    }
}