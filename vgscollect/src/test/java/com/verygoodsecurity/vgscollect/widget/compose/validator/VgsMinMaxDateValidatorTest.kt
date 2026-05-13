package com.verygoodsecurity.vgscollect.widget.compose.validator

import com.verygoodsecurity.vgscollect.widget.compose.date.VgsExpiryDateFormat
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VgsMinMaxDateValidatorTest {

    private val format = VgsExpiryDateFormat.MonthLongYear

    // Range: January 2020 – January 2099
    // Dates computed with the same format to stay timezone-agnostic.
    private val minDate = format.parse("012020")!!.time
    private val maxDate = format.parse("012099")!!.time
    private val validator = VgsMinMaxDateValidator(minDate, maxDate, format)

    @Test
    fun validate_dateWithinRange_validatedSuccessfully() {
        assertTrue(validator.validate("062050").isValid)
    }

    @Test
    fun validate_dateAtRangeMin_validatedSuccessfully() {
        assertTrue(validator.validate("012020").isValid)
    }

    @Test
    fun validate_dateAtRangeMax_validatedSuccessfully() {
        assertTrue(validator.validate("012099").isValid)
    }

    @Test
    fun validate_dateBeforeMin_validationFailed() {
        assertFalse(validator.validate("012019").isValid)
    }

    @Test
    fun validate_dateAfterMax_validationFailed() {
        // February 2099 is after January 2099
        assertFalse(validator.validate("022099").isValid)
    }

    @Test
    fun validate_emptyText_validationFailed() {
        assertFalse(validator.validate("").isValid)
    }

    @Test
    fun validate_unparseableText_validationFailed() {
        assertFalse(validator.validate("notadate").isValid)
    }
}
