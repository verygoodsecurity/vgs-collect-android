package com.verygoodsecurity.vgscollect.card.validator

import com.verygoodsecurity.vgscollect.view.card.validation.card.CardNumberValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CardNumberValidatorTest {
    private val validator = CardNumberValidator()

    @Before
    fun clearAll() {
        validator.clearRules()
    }

    @Test
    fun customDivider() {
        val validator = CardNumberValidator("-")
        validator.addRule("^(5[1-5][0-9]{4}|677189)|^(222[1-9]|2[3-6]\\d{2}|27[0-1]\\d|2720)([0-9]{2})")
        assertTrue(validator.isValid("5555-5555-5555-5555"))
    }

    @Test
    fun withoutRules() {
        assertFalse(validator.isValid("1243 23"))
    }

    @Test
    fun valid1Rule() {
        validator.addRule("^(5[1-5][0-9]{4}|677189)|^(222[1-9]|2[3-6]\\d{2}|27[0-1]\\d|2720)([0-9]{2})")

        assertTrue(validator.isValid("5555 5555 5555 5555"))
    }

    @Test
    fun validMultipleRules() {
        validator.addRule("^123")
        validator.addRule("^782")
        validator.addRule("^(5[1-5][0-9]{4}|677189)|^(222[1-9]|2[3-6]\\d{2}|27[0-1]\\d|2720)([0-9]{2})")

        assertTrue(validator.isValid("123"))
        assertTrue(validator.isValid("7822 3445 64"))
        assertTrue(validator.isValid("5555 5555 5555 5555"))
    }

}