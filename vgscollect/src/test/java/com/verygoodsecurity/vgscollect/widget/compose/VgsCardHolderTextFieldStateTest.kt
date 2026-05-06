package com.verygoodsecurity.vgscollect.widget.compose

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VgsCardHolderTextFieldStateTest {

    private fun stateWith(text: String) = VgsCardHolderTextFieldState("field").copy(text = text)

    @Test
    fun validate_lettersOnly_validatedSuccessfully() {
        assertTrue(stateWith("John").isValid)
    }

    @Test
    fun validate_nameWithSpace_validatedSuccessfully() {
        assertTrue(stateWith("John Doe").isValid)
    }

    @Test
    fun validate_nameWithHyphen_validatedSuccessfully() {
        assertTrue(stateWith("Mary-Jane").isValid)
    }

    @Test
    fun validate_nameWithApostrophe_validatedSuccessfully() {
        assertTrue(stateWith("O'Brien").isValid)
    }

    @Test
    fun validate_nameWithPeriod_validatedSuccessfully() {
        assertTrue(stateWith("Dr.Smith").isValid)
    }

    @Test
    fun validate_nameWithComma_validatedSuccessfully() {
        assertTrue(stateWith("Smith,John").isValid)
    }

    @Test
    fun validate_nameWithNumbers_validatedSuccessfully() {
        assertTrue(stateWith("Agent007").isValid)
    }

    @Test
    fun validate_emptyName_validationFailed() {
        assertFalse(stateWith("").isValid)
    }

    @Test
    fun validate_nameWithAtSign_validationFailed() {
        assertFalse(stateWith("john@doe").isValid)
    }

    @Test
    fun validate_nameWithHashSign_validationFailed() {
        assertFalse(stateWith("john#doe").isValid)
    }

    @Test
    fun validate_nameWithSlash_validationFailed() {
        assertFalse(stateWith("john/doe").isValid)
    }

    @Test
    fun validate_whitespaceOnly_validationFailed() {
        assertFalse(stateWith("   ").isValid)
    }
}
