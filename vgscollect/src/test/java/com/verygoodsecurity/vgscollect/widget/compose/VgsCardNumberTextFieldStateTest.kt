package com.verygoodsecurity.vgscollect.widget.compose

import com.verygoodsecurity.vgscollect.widget.compose.card.VgsCardBrand
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VgsCardNumberTextFieldStateTest {

    private fun stateWith(text: String) = VgsCardNumberTextFieldState("field").copy(text = text)

    // Validation

    @Test
    fun validate_validVisaCard_validatedSuccessfully() {
        assertTrue(stateWith("4111111111111111").isValid)
    }

    @Test
    fun validate_validAmexCard_validatedSuccessfully() {
        assertTrue(stateWith("378282246310005").isValid)
    }

    @Test
    fun validate_invalidLuhnVisaCard_validationFailed() {
        assertFalse(stateWith("4111111111111112").isValid)
    }

    @Test
    fun validate_tooShortVisaCard_validationFailed() {
        assertFalse(stateWith("411111111111111").isValid)
    }

    @Test
    fun validate_emptyCard_validationFailed() {
        assertFalse(stateWith("").isValid)
    }

    @Test
    fun validate_brandValidationDisabled_nonEmptyCard_validatedSuccessfully() {
        val state = VgsCardNumberTextFieldState(
            fieldName = "field",
            isCardBrandValidationEnabled = false
        ).copy(text = "123")
        assertTrue(state.isValid)
    }

    @Test
    fun validate_brandValidationDisabled_emptyCard_validationFailed() {
        val state = VgsCardNumberTextFieldState(
            fieldName = "field",
            isCardBrandValidationEnabled = false
        ).copy(text = "")
        assertFalse(state.isValid)
    }

    @Test
    fun validate_formattedWithSpaces_normalizedAndValidatedSuccessfully() {
        assertTrue(stateWith("4111 1111 1111 1111").isValid)
    }

    // Brand detection

    @Test
    fun cardBrand_visaNumber_detectedAsVisa() {
        assertEquals("VISA", stateWith("4111111111111111").cardBrand.name)
    }

    @Test
    fun cardBrand_amexNumber_detectedAsAmex() {
        assertEquals("AMERICAN_EXPRESS", stateWith("378282246310005").cardBrand.name)
    }

    @Test
    fun cardBrand_emptyText_remainedUnknown() {
        assertEquals(VgsCardBrand.UNKNOWN, stateWith("").cardBrand)
    }

    @Test
    fun cardBrand_unsupportedBrand_remainedUnknown() {
        val state = VgsCardNumberTextFieldState(
            fieldName = "field",
            supportedCardBrands = emptyList()
        ).copy(text = "4111111111111111")
        assertEquals(VgsCardBrand.UNKNOWN, state.cardBrand)
    }

    // BIN and last4

    @Test
    fun bin_validVisaCard_eightDigitsReturned() {
        assertEquals("41111111", stateWith("4111111111111111").bin)
    }

    @Test
    fun bin_validAmexCard_sixDigitsReturned() {
        assertEquals("378282", stateWith("378282246310005").bin)
    }

    @Test
    fun bin_invalidCard_nullReturned() {
        assertNull(stateWith("4111111111111112").bin)
    }

    @Test
    fun last4_validCard_fourDigitsReturned() {
        assertEquals("1111", stateWith("4111111111111111").last4)
    }

    @Test
    fun last4_invalidCard_nullReturned() {
        assertNull(stateWith("4111111111111112").last4)
    }

    // Normalization

    @Test
    fun copy_amexNumberTooLong_truncatedToFifteenDigits() {
        val state = stateWith("3782822463100050") // 16 digits starting with 37
        assertEquals(15, state.text.length)
    }
}
