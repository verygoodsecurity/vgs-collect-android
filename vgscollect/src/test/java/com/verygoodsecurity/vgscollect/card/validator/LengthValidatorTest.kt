package com.verygoodsecurity.vgscollect.card.validator

import com.verygoodsecurity.vgscollect.view.card.validation.LengthValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LengthValidatorTest {
    private lateinit var validator: LengthValidator

    @Before
    fun setupValidator() {
        val array = (7..12).toMutableList().toTypedArray()
        validator = LengthValidator(array)
    }

    @Test
    fun test_less() {
        assertFalse(validator.isValid("123abc"))
        assertFalse(validator.isValid("val"))
    }

    @Test
    fun test_valid() {
        assertTrue(validator.isValid("123 890 t"))
        assertTrue(validator.isValid("123 890 te"))
        assertTrue(validator.isValid("123 890 tes"))
        assertTrue(validator.isValid("123 890 test"))
    }

    @Test
    fun test_more() {
        assertFalse(validator.isValid("123 890 test tes"))
        assertFalse(validator.isValid("123 890 test test"))
    }

}