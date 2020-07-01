package com.verygoodsecurity.vgscollect.card.validator

import com.verygoodsecurity.vgscollect.view.card.validation.RegexValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CardHolderValidatorTest {
    private lateinit var validator:RegexValidator

    @Before
    fun setupValidator() {
        validator = RegexValidator("^[a-zA-Z0-9 ,'.-]+\$")
    }

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
        assertTrue(validator.isValid("abra cadab ra"))
    }

    @Test
    fun test_custom_regex() {
        validator.setRegex("^([a-zA-Z]{2,}\\s[a-zA-z]{1,})\$")

        assertFalse(validator.isValid("abra"))
        assertTrue(validator.isValid("abra cadabra"))
        assertFalse(validator.isValid("abra cadab ra"))
        assertFalse(validator.isValid("qq aa$"))
    }

    @Test
    fun testEmptyValidator() {
        val validator = RegexValidator()

        assertTrue(validator.isValid(""))
        assertTrue(validator.isValid(null))
        assertTrue(validator.isValid("abra"))
        assertTrue(validator.isValid("abra cadabra"))
        assertTrue(validator.isValid("abra cadab ra"))
        assertTrue(validator.isValid("qq aa$"))
        assertTrue(validator.isValid("( * 2q aa$"))
    }

}