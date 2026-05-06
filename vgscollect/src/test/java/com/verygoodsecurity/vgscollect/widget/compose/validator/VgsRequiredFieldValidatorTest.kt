package com.verygoodsecurity.vgscollect.widget.compose.validator

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VgsRequiredFieldValidatorTest {

    private val validator = VgsRequiredFieldValidator()

    @Test
    fun validate_nonEmptyText_validatedSuccessfully() {
        assertTrue(validator.validate("hello").isValid)
    }

    @Test
    fun validate_emptyText_validationFailed() {
        assertFalse(validator.validate("").isValid)
    }

    @Test
    fun validate_whitespaceOnly_validationFailed() {
        assertFalse(validator.validate("   ").isValid)
    }

    @Test
    fun validate_singleSpace_validationFailed() {
        assertFalse(validator.validate(" ").isValid)
    }
}
