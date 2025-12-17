package com.verygoodsecurity.vgscollect.view.card.validation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ABARoutingNumberValidatorTest {

    private val validator = ABARoutingNumberValidator()

    @Test
    fun test_valid_aba_routing_number() {
        assertTrue(validator.isValid("122100024"))
    }

    @Test
    fun test_invalid_aba_routing_number() {
        assertFalse(validator.isValid("123456789"))
    }

    @Test
    fun test_empty_string() {
        assertFalse(validator.isValid(""))
    }

    @Test
    fun test_short_string() {
        assertFalse(validator.isValid("123"))
    }

    @Test
    fun test_long_string() {
        assertFalse(validator.isValid("1234567890"))
    }

    @Test
    fun test_string_with_non_digits() {
        assertFalse(validator.isValid("12345678a"))
    }

    @Test
    fun test_string_with_zero_sum() {
        assertFalse(validator.isValid("000000000"))
    }
}
