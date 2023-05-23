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
        val twentyOneYearForwardCurrentTime = currentTime.yearsForward(21)

        assertTrue(validator.isValid(getFormattedDate(pattern, currentTime)))
        assertFalse(validator.isValid(getFormattedDate(pattern, yearBeforeCurrentTime)))
        assertFalse(validator.isValid(getFormattedDate(pattern, twentyOneYearForwardCurrentTime)))
    }

    @Test
    fun isValid_dash_dd_MM_yyyy_minMaxDetected() {
        val pattern = "dd-MM-yyyy"
        val validator = createValidator(pattern)
        val currentTime = System.currentTimeMillis()
        val yearBeforeCurrentTime = currentTime.yearsRewind(1)
        val twentyOneYearForwardCurrentTime = currentTime.yearsForward(21)

        assertTrue(validator.isValid(getFormattedDate(pattern, currentTime)))
        assertFalse(validator.isValid(getFormattedDate(pattern, yearBeforeCurrentTime)))
        assertFalse(validator.isValid(getFormattedDate(pattern, twentyOneYearForwardCurrentTime)))
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

    @Test
    fun isValid_slash_dd_MM_yyyy_customMinMaxDate() {
        val currentTime = System.currentTimeMillis()
        val pattern = "dd/MM/yyyy"

        val minDate = currentTime
            .yearsRewind(1)
            .monthsRewind(1)
            .daysRewind(1)

        val maxDate = currentTime
            .yearsForward(1)
            .monthsForward(1)
            .daysForward(1)

        val validator = createValidator(
            pattern,
            minDate,
            maxDate,
            daysVisible = true,
            inclusive = true
        )

        val yearBeforeCurrentTime = currentTime.yearsRewind(1)
        val monthBeforeCurrentTime = currentTime.monthsRewind(1)
        val dayBeforeCurrentTime = currentTime.daysRewind(1)
        val yearAfterCurrentTime = currentTime.yearsForward(1)
        val monthAfterCurrentTime = currentTime.monthsForward(1)
        val dayAfterCurrentTime = currentTime.daysForward(1)
        val invalidMinDateTimeByOneDay = currentTime
            .yearsRewind(1)
            .monthsRewind(1)
            .daysRewind(2)
        val invalidMinDateTimeByOneMonth = currentTime
            .yearsRewind(1)
            .monthsRewind(2)
            .daysRewind(1)
        val invalidMinDateTimeByOneYear = currentTime
            .yearsRewind(2)
            .monthsRewind(1)
            .daysRewind(1)
        val invalidMaxDateTimeByOneDay = currentTime
            .yearsForward(1)
            .monthsForward(1)
            .daysForward(2)
        val invalidMaxDateTimeByOneMonth = currentTime
            .yearsForward(1)
            .monthsForward(2)
            .daysForward(1)
        val invalidMaxDateTimeByOneYear = currentTime
            .yearsForward(2)
            .monthsForward(1)
            .daysForward(1)

        assertTrue(validator.isValid(getFormattedDate(pattern, currentTime)))
        assertTrue(validator.isValid(getFormattedDate(pattern, yearBeforeCurrentTime)))
        assertTrue(validator.isValid(getFormattedDate(pattern, monthBeforeCurrentTime)))
        assertTrue(validator.isValid(getFormattedDate(pattern, dayBeforeCurrentTime)))
        assertTrue(validator.isValid(getFormattedDate(pattern, yearAfterCurrentTime)))
        assertTrue(validator.isValid(getFormattedDate(pattern, monthAfterCurrentTime)))
        assertTrue(validator.isValid(getFormattedDate(pattern, dayAfterCurrentTime)))
        assertFalse(validator.isValid(getFormattedDate(pattern, invalidMinDateTimeByOneDay)))
        assertFalse(validator.isValid(getFormattedDate(pattern, invalidMinDateTimeByOneMonth)))
        assertFalse(validator.isValid(getFormattedDate(pattern, invalidMinDateTimeByOneYear)))
        assertFalse(validator.isValid(getFormattedDate(pattern, invalidMaxDateTimeByOneDay)))
        assertFalse(validator.isValid(getFormattedDate(pattern, invalidMaxDateTimeByOneMonth)))
        assertFalse(validator.isValid(getFormattedDate(pattern, invalidMaxDateTimeByOneYear)))
    }

    @Test
    fun isValid_dash_dd_MM_yyyy_customMinMaxDate() {
        val currentTime = System.currentTimeMillis()
        val pattern = "dd-MM-yyyy"

        val minDate = currentTime
            .yearsRewind(1)
            .monthsRewind(1)
            .daysRewind(1)

        val maxDate = currentTime
            .yearsForward(1)
            .monthsForward(1)
            .daysForward(1)

        val validator = createValidator(
            pattern,
            minDate,
            maxDate,
            daysVisible = true,
            inclusive = true
        )

        val yearBeforeCurrentTime = currentTime.yearsRewind(1)
        val monthBeforeCurrentTime = currentTime.monthsRewind(1)
        val dayBeforeCurrentTime = currentTime.daysRewind(1)
        val yearAfterCurrentTime = currentTime.yearsForward(1)
        val monthAfterCurrentTime = currentTime.monthsForward(1)
        val dayAfterCurrentTime = currentTime.daysForward(1)
        val invalidMinDateTimeByOneDay = currentTime
            .yearsRewind(1)
            .monthsRewind(1)
            .daysRewind(2)
        val invalidMinDateTimeByOneMonth = currentTime
            .yearsRewind(1)
            .monthsRewind(2)
            .daysRewind(1)
        val invalidMinDateTimeByOneYear = currentTime
            .yearsRewind(2)
            .monthsRewind(1)
            .daysRewind(1)
        val invalidMaxDateTimeByOneDay = currentTime
            .yearsForward(1)
            .monthsForward(1)
            .daysForward(2)
        val invalidMaxDateTimeByOneMonth = currentTime
            .yearsForward(1)
            .monthsForward(2)
            .daysForward(1)
        val invalidMaxDateTimeByOneYear = currentTime
            .yearsForward(2)
            .monthsForward(1)
            .daysForward(1)

        assertTrue(validator.isValid(getFormattedDate(pattern, currentTime)))
        assertTrue(validator.isValid(getFormattedDate(pattern, yearBeforeCurrentTime)))
        assertTrue(validator.isValid(getFormattedDate(pattern, monthBeforeCurrentTime)))
        assertTrue(validator.isValid(getFormattedDate(pattern, dayBeforeCurrentTime)))
        assertTrue(validator.isValid(getFormattedDate(pattern, yearAfterCurrentTime)))
        assertTrue(validator.isValid(getFormattedDate(pattern, monthAfterCurrentTime)))
        assertTrue(validator.isValid(getFormattedDate(pattern, dayAfterCurrentTime)))
        assertFalse(validator.isValid(getFormattedDate(pattern, invalidMinDateTimeByOneDay)))
        assertFalse(validator.isValid(getFormattedDate(pattern, invalidMinDateTimeByOneMonth)))
        assertFalse(validator.isValid(getFormattedDate(pattern, invalidMinDateTimeByOneYear)))
        assertFalse(validator.isValid(getFormattedDate(pattern, invalidMaxDateTimeByOneDay)))
        assertFalse(validator.isValid(getFormattedDate(pattern, invalidMaxDateTimeByOneMonth)))
        assertFalse(validator.isValid(getFormattedDate(pattern, invalidMaxDateTimeByOneYear)))
    }

    @Test
    fun isValid_slash_dd_MM_yyyy_customMinDate() {
        val currentTime = System.currentTimeMillis()
        val pattern = "dd/MM/yyyy"

        val minDate = currentTime
            .yearsRewind(1)
            .monthsRewind(1)
            .daysRewind(1)

        val validator = createValidator(
            pattern,
            minDate,
            null,
            daysVisible = true,
            inclusive = true
        )

        val yearBeforeCurrentTime = currentTime.yearsRewind(1)
        val monthBeforeCurrentTime = currentTime.monthsRewind(1)
        val dayBeforeCurrentTime = currentTime.daysRewind(1)
        val yearAfterCurrentTime = currentTime.yearsForward(1)
        val monthAfterCurrentTime = currentTime.monthsForward(1)
        val dayAfterCurrentTime = currentTime.daysForward(1)
        val invalidMinDateTimeByOneDay = currentTime
            .yearsRewind(1)
            .monthsRewind(1)
            .daysRewind(2)
        val invalidMinDateTimeByOneMonth = currentTime
            .yearsRewind(1)
            .monthsRewind(2)
            .daysRewind(1)
        val invalidMinDateTimeByOneYear = currentTime
            .yearsRewind(2)
            .monthsRewind(1)
            .daysRewind(1)
        val maxDateTimeByOneDay = currentTime
            .yearsForward(1)
            .monthsForward(1)
            .daysForward(2)
        val maxDateTimeByOneMonth = currentTime
            .yearsForward(1)
            .monthsForward(2)
            .daysForward(1)
        val maxDateTimeByOneYear = currentTime
            .yearsForward(2)
            .monthsForward(1)
            .daysForward(1)

        assertTrue(validator.isValid(getFormattedDate(pattern, currentTime)))
        assertTrue(validator.isValid(getFormattedDate(pattern, yearBeforeCurrentTime)))
        assertTrue(validator.isValid(getFormattedDate(pattern, monthBeforeCurrentTime)))
        assertTrue(validator.isValid(getFormattedDate(pattern, dayBeforeCurrentTime)))
        assertTrue(validator.isValid(getFormattedDate(pattern, yearAfterCurrentTime)))
        assertTrue(validator.isValid(getFormattedDate(pattern, monthAfterCurrentTime)))
        assertTrue(validator.isValid(getFormattedDate(pattern, dayAfterCurrentTime)))
        assertFalse(validator.isValid(getFormattedDate(pattern, invalidMinDateTimeByOneDay)))
        assertFalse(validator.isValid(getFormattedDate(pattern, invalidMinDateTimeByOneMonth)))
        assertFalse(validator.isValid(getFormattedDate(pattern, invalidMinDateTimeByOneYear)))
        assertTrue(validator.isValid(getFormattedDate(pattern, maxDateTimeByOneDay)))
        assertTrue(validator.isValid(getFormattedDate(pattern, maxDateTimeByOneMonth)))
        assertTrue(validator.isValid(getFormattedDate(pattern, maxDateTimeByOneYear)))
    }

    @Test
    fun isValid_slash_dd_MM_yyyy_customMaxDate() {
        val currentTime = System.currentTimeMillis()
        val pattern = "dd/MM/yyyy"

        val maxDate = currentTime
            .yearsForward(1)
            .monthsForward(1)
            .daysForward(1)

        val validator = createValidator(
            pattern,
            null,
            maxDate,
            daysVisible = true,
            inclusive = true
        )

        val yearBeforeCurrentTime = currentTime.yearsRewind(1)
        val monthBeforeCurrentTime = currentTime.monthsRewind(1)
        val dayBeforeCurrentTime = currentTime.daysRewind(1)
        val yearAfterCurrentTime = currentTime.yearsForward(1)
        val monthAfterCurrentTime = currentTime.monthsForward(1)
        val dayAfterCurrentTime = currentTime.daysForward(1)
        val minDateTimeByOneDay = currentTime
            .yearsRewind(1)
            .monthsRewind(1)
            .daysRewind(2)
        val minDateTimeByOneMonth = currentTime
            .yearsRewind(1)
            .monthsRewind(2)
            .daysRewind(1)
        val minDateTimeByOneYear = currentTime
            .yearsRewind(2)
            .monthsRewind(1)
            .daysRewind(1)
        val invalidMaxDateTimeByOneDay = currentTime
            .yearsForward(1)
            .monthsForward(1)
            .daysForward(2)
        val invalidMaxDateTimeByOneMonth = currentTime
            .yearsForward(1)
            .monthsForward(2)
            .daysForward(1)
        val invalidMaxDateTimeByOneYear = currentTime
            .yearsForward(2)
            .monthsForward(1)
            .daysForward(1)

        assertTrue(validator.isValid(getFormattedDate(pattern, currentTime)))
        assertTrue(validator.isValid(getFormattedDate(pattern, yearBeforeCurrentTime)))
        assertTrue(validator.isValid(getFormattedDate(pattern, monthBeforeCurrentTime)))
        assertTrue(validator.isValid(getFormattedDate(pattern, dayBeforeCurrentTime)))
        assertTrue(validator.isValid(getFormattedDate(pattern, yearAfterCurrentTime)))
        assertTrue(validator.isValid(getFormattedDate(pattern, monthAfterCurrentTime)))
        assertTrue(validator.isValid(getFormattedDate(pattern, dayAfterCurrentTime)))
        assertTrue(validator.isValid(getFormattedDate(pattern, minDateTimeByOneDay)))
        assertTrue(validator.isValid(getFormattedDate(pattern, minDateTimeByOneMonth)))
        assertTrue(validator.isValid(getFormattedDate(pattern, minDateTimeByOneYear)))
        assertFalse(validator.isValid(getFormattedDate(pattern, invalidMaxDateTimeByOneDay)))
        assertFalse(validator.isValid(getFormattedDate(pattern, invalidMaxDateTimeByOneMonth)))
        assertFalse(validator.isValid(getFormattedDate(pattern, invalidMaxDateTimeByOneYear)))
    }

    @Test
    fun isValid_inputDateChanged() {
        val pattern = "dd-MM-yyyy"
        val validator = createValidator(pattern)
        val otherPattern = "yyyy-MM-dd"

        assertFalse(validator.isValid(getFormattedDate(otherPattern, System.currentTimeMillis())))
    }

    @Test
    fun isValid_inclusive() {
        val currentTime = System.currentTimeMillis()
        val pattern = "dd/MM/yyyy"

        val minDate = currentTime
            .yearsRewind(1)
            .monthsRewind(1)
            .daysRewind(1)

        val maxDate = currentTime
            .yearsForward(1)
            .monthsForward(1)
            .daysForward(1)

        val validator = createValidator(
            pattern,
            minDate,
            maxDate,
            daysVisible = true,
            inclusive = true
        )

        val dayBeforeMinDate = minDate.daysRewind(1)
        val dayAfterMaxDate = maxDate.daysForward(1)

        assertTrue(validator.isValid(getFormattedDate(pattern, currentTime)))
        assertFalse(validator.isValid(getFormattedDate(pattern, dayBeforeMinDate)))
        assertFalse(validator.isValid(getFormattedDate(pattern, dayAfterMaxDate)))
        assertTrue(validator.isValid(getFormattedDate(pattern, minDate)))
        assertTrue(validator.isValid(getFormattedDate(pattern, maxDate)))
    }

    @Test
    fun isValid_not_inclusive() {
        val currentTime = System.currentTimeMillis()
        val pattern = "dd/MM/yyyy"

        val minDate = currentTime
            .yearsRewind(1)
            .monthsRewind(1)
            .daysRewind(1)

        val maxDate = currentTime
            .yearsForward(1)
            .monthsForward(1)
            .daysForward(1)

        val validator = createValidator(
            pattern,
            minDate,
            maxDate,
            daysVisible = true,
            inclusive = false
        )

        val dayBeforeMinDate = minDate.daysRewind(1)
        val dayAfterMaxDate = maxDate.daysForward(1)

        assertTrue(validator.isValid(getFormattedDate(pattern, currentTime)))
        assertFalse(validator.isValid(getFormattedDate(pattern, dayBeforeMinDate)))
        assertFalse(validator.isValid(getFormattedDate(pattern, dayAfterMaxDate)))
        assertFalse(validator.isValid(getFormattedDate(pattern, minDate)))
        assertFalse(validator.isValid(getFormattedDate(pattern, maxDate)))
    }

    /**
     * Creates [com.verygoodsecurity.vgscollect.view.date.validation.TimeGapsValidator] with default
     * min(current time) and max(twenty years forward) date range.
     */
    private fun createValidator(
        pattern: String,
        min: Long? = System.currentTimeMillis(),
        max: Long? = min?.yearsForward(20),
        daysVisible: Boolean = false,
        inclusive: Boolean = false
    ): TimeGapsValidator = TimeGapsValidator(pattern, daysVisible, inclusive, min, max)

    private fun Long.yearsForward(years: Int): Long = Calendar.getInstance().apply {
        timeInMillis = this@yearsForward
        add(Calendar.YEAR, years)
        setMaximumTime()
    }.timeInMillis

    private fun Long.monthsForward(months: Int): Long = Calendar.getInstance().apply {
        timeInMillis = this@monthsForward
        add(Calendar.MONTH, months)
        setMaximumTime()
    }.timeInMillis

    private fun Long.daysForward(days: Int): Long = Calendar.getInstance().apply {
        timeInMillis = this@daysForward
        add(Calendar.DAY_OF_MONTH, days)
        setMaximumTime()
    }.timeInMillis

    private fun Long.yearsRewind(years: Int): Long = Calendar.getInstance().apply {
        timeInMillis = this@yearsRewind
        add(Calendar.YEAR, -years)
        setMaximumTime()
    }.timeInMillis

    private fun Long.monthsRewind(months: Int): Long = Calendar.getInstance().apply {
        timeInMillis = this@monthsRewind
        add(Calendar.MONTH, -months)
        setMaximumTime()
    }.timeInMillis

    private fun Long.daysRewind(days: Int): Long = Calendar.getInstance().apply {
        timeInMillis = this@daysRewind
        add(Calendar.DAY_OF_MONTH, -days)
        setMaximumTime()
    }.timeInMillis

    private fun Calendar.setMaximumTime() {
        this.apply {
            set(Calendar.HOUR_OF_DAY, getActualMaximum(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, getActualMaximum(Calendar.MINUTE))
            set(Calendar.SECOND, getActualMaximum(Calendar.SECOND))
            set(Calendar.MILLISECOND, getActualMaximum(Calendar.MILLISECOND))
        }
    }

    private fun getFormattedDate(
        pattern: String,
        time: Long
    ): String = SimpleDateFormat(pattern).format(time)
}