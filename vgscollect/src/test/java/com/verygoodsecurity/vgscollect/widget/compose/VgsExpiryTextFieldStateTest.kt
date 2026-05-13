package com.verygoodsecurity.vgscollect.widget.compose

import com.verygoodsecurity.vgscollect.widget.compose.date.VgsExpiryDateFormat
import com.verygoodsecurity.vgscollect.widget.compose.state.VgsExpiryTextFieldState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VgsExpiryTextFieldStateTest {

    // Using MonthLongYear to avoid 2-digit year window ambiguity
    private fun stateWith(text: String) = VgsExpiryTextFieldState(
        fieldName = "field",
        inputDateFormat = VgsExpiryDateFormat.MonthLongYear
    ).copy(text = text)

    // "122099" → "12/2099" → December 2099 → far future
    // "012020" → "01/2020" → January 2020 → past

    @Test
    fun validate_futureDate_validatedSuccessfully() {
        assertTrue(stateWith("122099").isValid)
    }

    @Test
    fun validate_pastDate_validationFailed() {
        assertFalse(stateWith("012020").isValid)
    }

    @Test
    fun validate_emptyText_validationFailed() {
        assertFalse(stateWith("").isValid)
    }

    @Test
    fun validate_formattedWithSlash_normalizedAndValidatedSuccessfully() {
        assertTrue(stateWith("12/2099").isValid)
    }

    @Test
    fun copy_inputLongerThanFormat_truncatedToSixDigits() {
        assertEquals(6, stateWith("1220991").text.length)
    }

    @Test
    fun getOutputText_futureDate_formattedDateReturned() {
        assertEquals("12/2099", stateWith("122099").getOutputText())
    }

    @Test
    fun getOutputText_emptyText_emptyStringReturned() {
        assertEquals("", stateWith("").getOutputText())
    }
}
