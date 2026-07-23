package com.verygoodsecurity.vgscollect.widget.compose.validator

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VgsTextLengthValidatorTest {

    @Test
    fun validate_textMatchesSingleAllowedLength_validatedSuccessfully() {
        assertTrue(VgsTextLengthValidator(arrayOf(4)).validate("1234").isValid)
    }

    @Test
    fun validate_textShorterThanAllowedLength_validationFailed() {
        assertFalse(VgsTextLengthValidator(arrayOf(4)).validate("123").isValid)
    }

    @Test
    fun validate_textLongerThanAllowedLength_validationFailed() {
        assertFalse(VgsTextLengthValidator(arrayOf(4)).validate("12345").isValid)
    }

    @Test
    fun validate_emptyText_validationFailed() {
        assertFalse(VgsTextLengthValidator(arrayOf(4)).validate("").isValid)
    }

    @Test
    fun validate_textMatchesOneOfMultipleAllowedLengths_validatedSuccessfully() {
        val validator = VgsTextLengthValidator(arrayOf(3, 4))
        assertTrue(validator.validate("123").isValid)
        assertTrue(validator.validate("1234").isValid)
    }

    @Test
    fun validate_textMatchesNoneOfMultipleAllowedLengths_validationFailed() {
        assertFalse(VgsTextLengthValidator(arrayOf(3, 4)).validate("12345").isValid)
    }

    @Test
    fun validate_emptyTextWithZeroAllowedLength_validatedSuccessfully() {
        assertTrue(VgsTextLengthValidator(arrayOf(0)).validate("").isValid)
    }
}
