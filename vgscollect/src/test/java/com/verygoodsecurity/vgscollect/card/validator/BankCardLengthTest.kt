package com.verygoodsecurity.vgscollect.card.validator

import com.verygoodsecurity.vgscollect.view.card.validation.LengthValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BankCardLengthTest {

    @Test
    fun test_length() {
        val array = arrayOf(12,15,19)
        val validator = LengthValidator(array)

        assertTrue(validator.isValid("123456789012"))
        assertTrue(validator.isValid("123456789012345"))
        assertTrue(validator.isValid("1234567890123456789"))
        assertFalse(validator.isValid("1234567890123456"))
    }

    @Test
    fun test_2() {
        val validator = LengthValidator(arrayOf())

        assertFalse(validator.isValid("12"))
        assertFalse(validator.isValid("1"))
        assertFalse(validator.isValid(""))
    }
}