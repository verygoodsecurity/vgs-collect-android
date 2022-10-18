package com.verygoodsecurity.vgscollect.date

import com.verygoodsecurity.vgscollect.view.date.validation.TimeGapsValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class TimeGapsValidatorTest {

    @Test
    fun isValid_slash_dd_MM_yyyy_minMaxDetected() {
        val pattern = "dd/MM/yyyy"
        val validator = createValidator(pattern)
        val currentTime = System.currentTimeMillis()
        val yearBeforeCurrentTime = currentTime.yearsRewind(1)
        val threeYearsForwardCurrentTime = currentTime.yearsForward(3)

        assertTrue(validator.isValid(getFormattedDate(pattern, currentTime)))
        assertFalse(validator.isValid(getFormattedDate(pattern, yearBeforeCurrentTime)))
        assertFalse(validator.isValid(getFormattedDate(pattern, threeYearsForwardCurrentTime)))
    }

    @Test
    fun isValid_dash_dd_MM_yyyy_minMaxDetected() {
        val pattern = "dd-MM-yyyy"
        val validator = createValidator(pattern)
        val currentTime = System.currentTimeMillis()
        val yearBeforeCurrentTime = currentTime.yearsRewind(1)
        val threeYearsForwardCurrentTime = currentTime.yearsForward(3)

        assertTrue(validator.isValid(getFormattedDate(pattern, currentTime)))
        assertFalse(validator.isValid(getFormattedDate(pattern, yearBeforeCurrentTime)))
        assertFalse(validator.isValid(getFormattedDate(pattern, threeYearsForwardCurrentTime)))
    }

    @Test
    fun isValid_inputDateIncorrectFormat() {
        val pattern = "dd-MM-yyyy"
        val validator = createValidator(pattern)
        val otherPattern = "dd/MM/yyyy"

        assertFalse(validator.isValid(getFormattedDate(otherPattern, System.currentTimeMillis())))
    }

    @Test
    fun isValid_inputDateShorter() {
        val pattern = "dd-MM-yyyy"
        val validator = createValidator(pattern)
        val otherPattern = "MM-yyyy"

        assertFalse(validator.isValid(getFormattedDate(otherPattern, System.currentTimeMillis())))
    }

    @Test
    fun isValid_inputDateLonger() {
        val pattern = "MM-yyyy"
        val validator = createValidator(pattern)
        val otherPattern = "dd-MM-yyyy"

        assertFalse(validator.isValid(getFormattedDate(otherPattern, System.currentTimeMillis())))
    }

    @Test
    fun isValid_inputDateInvalid() {
        val pattern = "dd-MM-yyyy"
        val validator = createValidator(pattern)

        assertFalse(validator.isValid(""))
        assertFalse(validator.isValid("test"))
        assertFalse(validator.isValid("dd-MM-yyyy"))
        assertFalse(validator.isValid("00-11-2222"))
    }

    /**
     * Creates [com.verygoodsecurity.vgscollect.view.date.validation.TimeGapsValidator] with default
     * min(current time) and max(two years forward) date range.
     */
    private fun createValidator(
        pattern: String,
        min: Long = System.currentTimeMillis(),
        max: Long = min.yearsForward(2)
    ): TimeGapsValidator = TimeGapsValidator(pattern, min, max)

    private fun Long.yearsForward(years: Int): Long = Calendar.getInstance().apply {
        timeInMillis = this@yearsForward
        add(Calendar.YEAR, years)
    }.timeInMillis

    private fun Long.yearsRewind(years: Int): Long = Calendar.getInstance().apply {
        timeInMillis = this@yearsRewind
        add(Calendar.YEAR, -years)
    }.timeInMillis

    private fun getFormattedDate(
        pattern: String,
        time: Long
    ): String = SimpleDateFormat(pattern).format(time)
}