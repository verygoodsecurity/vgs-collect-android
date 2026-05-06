package com.verygoodsecurity.vgscollect.widget.compose

import com.verygoodsecurity.vgscollect.widget.compose.card.VgsCardBrand
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VgsCvcTextFieldStateTest {

    private val amex = VgsCardBrand.detect("378282246310005")

    private fun stateWith(text: String) = VgsCvcTextFieldState("field").copy(text = text)

    // UNKNOWN brand accepts 3 or 4 digits

    @Test
    fun validate_threeDigitCvc_validatedSuccessfully() {
        assertTrue(stateWith("123").isValid)
    }

    @Test
    fun validate_fourDigitCvc_validatedSuccessfully() {
        assertTrue(stateWith("1234").isValid)
    }

    @Test
    fun validate_twoDigitCvc_validationFailed() {
        assertFalse(stateWith("12").isValid)
    }

    @Test
    fun validate_emptyText_validationFailed() {
        assertFalse(stateWith("").isValid)
    }

    @Test
    fun validate_fiveDigitCvc_truncatedToFourAndValidatedSuccessfully() {
        assertTrue(stateWith("12345").isValid)
    }

    @Test
    fun validate_brandValidationDisabled_threeDigitCvc_validatedSuccessfully() {
        val state = VgsCvcTextFieldState("field", isCardBrandValidationEnabled = false).copy(text = "123")
        assertTrue(state.isValid)
    }

    @Test
    fun validate_brandValidationDisabled_emptyText_validationFailed() {
        val state = VgsCvcTextFieldState("field", isCardBrandValidationEnabled = false).copy(text = "")
        assertFalse(state.isValid)
    }

    // withCardBrand (Amex requires exactly 4 digits)

    @Test
    fun withCardBrand_amexAndThreeDigitCvc_validationFailed() {
        assertFalse(stateWith("123").withCardBrand(amex).isValid)
    }

    @Test
    fun withCardBrand_amexAndFourDigitCvc_validatedSuccessfully() {
        assertTrue(stateWith("1234").withCardBrand(amex).isValid)
    }

    @Test
    fun withCardBrand_amexAndFiveDigitCvc_truncatedToFourAndValidatedSuccessfully() {
        val state = VgsCvcTextFieldState("field").withCardBrand(amex).copy(text = "12345")
        assertTrue(state.isValid)
    }
}
