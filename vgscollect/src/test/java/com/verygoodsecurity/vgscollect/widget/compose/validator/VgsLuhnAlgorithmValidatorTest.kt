package com.verygoodsecurity.vgscollect.widget.compose.validator

import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsLuhnAlgorithmValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VgsLuhnAlgorithmValidatorTest {

    private val validator = VgsLuhnAlgorithmValidator()

    @Test
    fun validate_validVisaCard_validatedSuccessfully() {
        assertTrue(validator.validate("4111111111111111").isValid)
    }

    @Test
    fun validate_validAmexCard_validatedSuccessfully() {
        assertTrue(validator.validate("378282246310005").isValid)
    }

    @Test
    fun validate_invalidChecksum_validationFailed() {
        assertFalse(validator.validate("4111111111111112").isValid)
    }

    @Test
    fun validate_emptyString_validationFailed() {
        assertFalse(validator.validate("").isValid)
    }

    @Test
    fun validate_blankString_validationFailed() {
        assertFalse(validator.validate("   ").isValid)
    }

    @Test
    fun validate_nonDigitChars_validationFailed() {
        assertFalse(validator.validate("4111-1111-1111-1111").isValid)
    }

    @Test
    fun validate_spaceSeparatedDigits_validationFailed() {
        assertFalse(validator.validate("4111 1111 1111 1111").isValid)
    }
}
