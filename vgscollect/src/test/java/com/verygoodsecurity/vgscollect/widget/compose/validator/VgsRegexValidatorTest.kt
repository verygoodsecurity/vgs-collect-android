package com.verygoodsecurity.vgscollect.widget.compose.validator

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VgsRegexValidatorTest {

    @Test
    fun validate_textMatchesRegex_validatedSuccessfully() {
        assertTrue(VgsRegexValidator("^[0-9]+$").validate("12345").isValid)
    }

    @Test
    fun validate_textDoesNotMatchRegex_validationFailed() {
        assertFalse(VgsRegexValidator("^[0-9]+$").validate("abc").isValid)
    }

    @Test
    fun validate_emptyTextWithMatchAllRegex_validatedSuccessfully() {
        assertTrue(VgsRegexValidator(".*").validate("").isValid)
    }

    @Test
    fun validate_emptyTextWithRequiredContentRegex_validationFailed() {
        assertFalse(VgsRegexValidator("^[0-9]+$").validate("").isValid)
    }

    @Test
    fun validate_partialMatchOnly_validationFailed() {
        // matches() requires full string match, not just find()
        assertFalse(VgsRegexValidator("^[0-9]+$").validate("123abc").isValid)
    }

    @Test
    fun validate_ssnRegex_validSsn_validatedSuccessfully() {
        val ssnRegex = "^(?!(000|666|9))\\d{3}(?!(00))\\d{2}(?!(0000))\\d{4}$"
        assertTrue(VgsRegexValidator(ssnRegex).validate("123456789").isValid)
    }

    @Test
    fun validate_ssnRegex_invalidAreaCode_validationFailed() {
        val ssnRegex = "^(?!(000|666|9))\\d{3}(?!(00))\\d{2}(?!(0000))\\d{4}$"
        assertFalse(VgsRegexValidator(ssnRegex).validate("000456789").isValid)
    }
}
