package com.verygoodsecurity.vgscollect.widget.compose

import com.verygoodsecurity.vgscollect.widget.compose.state.VgsSsnTextFieldState
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VgsSsnTextFieldStateTest {

    private fun stateWith(text: String) = VgsSsnTextFieldState("field").copy(text = text)

    @Test
    fun validate_validSsn_validatedSuccessfully() {
        assertTrue(stateWith("123456789").isValid)
    }

    @Test
    fun validate_minAreaCode_validatedSuccessfully() {
        assertTrue(stateWith("001010001").isValid)
    }

    @Test
    fun validate_areaCodeBelow666_validatedSuccessfully() {
        assertTrue(stateWith("665010001").isValid)
    }

    @Test
    fun validate_areaCodeAbove666_validatedSuccessfully() {
        assertTrue(stateWith("667010001").isValid)
    }

    @Test
    fun validate_areaCode800s_validatedSuccessfully() {
        assertTrue(stateWith("899010001").isValid)
    }

    @Test
    fun validate_areaCode000_validationFailed() {
        assertFalse(stateWith("000456789").isValid)
    }

    @Test
    fun validate_areaCode666_validationFailed() {
        assertFalse(stateWith("666456789").isValid)
    }

    @Test
    fun validate_areaCodeStartsWith9_validationFailed() {
        assertFalse(stateWith("900456789").isValid)
        assertFalse(stateWith("999456789").isValid)
    }

    @Test
    fun validate_groupCode00_validationFailed() {
        assertFalse(stateWith("123006789").isValid)
    }

    @Test
    fun validate_serialCode0000_validationFailed() {
        assertFalse(stateWith("123450000").isValid)
    }

    @Test
    fun validate_tooShort_validationFailed() {
        assertFalse(stateWith("12345678").isValid)
    }

    @Test
    fun validate_tooLong_truncatedToNineDigitsAndValidatedSuccessfully() {
        assertTrue(stateWith("1234567890").isValid)
    }

    @Test
    fun validate_empty_validationFailed() {
        assertFalse(stateWith("").isValid)
    }

    @Test
    fun validate_formattedWithDashes_normalizedAndValidatedSuccessfully() {
        assertTrue(stateWith("123-45-6789").isValid)
    }

    @Test
    fun validate_formattedWithSpaces_normalizedAndValidatedSuccessfully() {
        assertTrue(stateWith("123 45 6789").isValid)
    }
}
