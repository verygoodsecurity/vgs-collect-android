package com.verygoodsecurity.vgscollect.widget.compose

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VgsTextFieldStateTest {

    private fun stateWith(text: String) = VgsTextFieldState("field").copy(text = text)

    @Test
    fun validate_nonEmptyText_validatedSuccessfully() {
        assertTrue(stateWith("hello").isValid)
    }

    @Test
    fun validate_emptyText_validationFailed() {
        assertFalse(stateWith("").isValid)
    }

    @Test
    fun validate_whitespaceOnly_validationFailed() {
        assertFalse(stateWith("   ").isValid)
    }

    @Test
    fun validate_emptyValidatorsList_validatedSuccessfully() {
        assertTrue(VgsTextFieldState("field", emptyList()).copy(text = "").isValid)
    }
}
