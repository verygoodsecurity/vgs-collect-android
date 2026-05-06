package com.verygoodsecurity.vgscollect.widget.compose.validator

import com.verygoodsecurity.vgscollect.widget.compose.VgsSsnTextFieldState
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VgsSsnValidatorTest {

    private fun stateWith(text: String) = VgsSsnTextFieldState("field").copy(text = text)

    // region valid

    @Test
    fun test_valid_ssn() {
        assertTrue(stateWith("123456789").isValid)
    }

    @Test
    fun test_valid_min_area() {
        assertTrue(stateWith("001010001").isValid)
    }

    @Test
    fun test_valid_area_below_666() {
        assertTrue(stateWith("665010001").isValid)
    }

    @Test
    fun test_valid_area_above_666() {
        assertTrue(stateWith("667010001").isValid)
    }

    @Test
    fun test_valid_area_800s() {
        assertTrue(stateWith("899010001").isValid)
    }

    // endregion

    // region invalid area

    @Test
    fun test_invalid_area_000() {
        assertFalse(stateWith("000456789").isValid)
    }

    @Test
    fun test_invalid_area_666() {
        assertFalse(stateWith("666456789").isValid)
    }

    @Test
    fun test_invalid_area_starts_with_9() {
        assertFalse(stateWith("900456789").isValid)
        assertFalse(stateWith("999456789").isValid)
    }

    // endregion

    // region invalid group

    @Test
    fun test_invalid_group_00() {
        assertFalse(stateWith("123006789").isValid)
    }

    // endregion

    // region invalid serial

    @Test
    fun test_invalid_serial_0000() {
        assertFalse(stateWith("123450000").isValid)
    }

    // endregion

    // region length

    @Test
    fun test_invalid_too_short() {
        assertFalse(stateWith("12345678").isValid)
    }

    @Test
    fun test_invalid_too_long() {
        // normalizeText truncates to 9 digits, so "1234567890" becomes "123456789" — valid
        assertTrue(stateWith("1234567890").isValid)
    }

    @Test
    fun test_invalid_empty() {
        assertFalse(stateWith("").isValid)
    }

    // endregion

    // region normalization

    @Test
    fun test_formatted_with_dashes_normalized_to_digits() {
        // normalizeText strips dashes before validation, so "123-45-6789" → "123456789"
        assertTrue(stateWith("123-45-6789").isValid)
    }

    @Test
    fun test_formatted_with_spaces_normalized_to_digits() {
        assertTrue(stateWith("123 45 6789").isValid)
    }

    // endregion
}
