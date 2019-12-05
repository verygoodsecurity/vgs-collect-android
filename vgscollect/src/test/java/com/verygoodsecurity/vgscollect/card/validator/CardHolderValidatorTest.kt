package com.verygoodsecurity.vgscollect.card.validator

import com.verygoodsecurity.vgscollect.view.card.validation.CardHolderValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CardHolderValidatorTest {
    private val validator = CardHolderValidator()

    @Test
    fun name_with_special_symbols() {
        assertFalse(validator.isValid("qq aa$ qedds"))
    }

    @Test
    fun name_with_number() {
        assertTrue(validator.isValid("abra, cadabra3"))
    }

    @Test
    fun name_dafault() {
        assertTrue(validator.isValid("abra cadabra"))
    }

}