package com.verygoodsecurity.vgscollect.view.date

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class DateRangeFormatTest {

    //region - Companion
    companion object {
        private const val TEST_VALUE_1 = "12/09/2020"
        private const val TEST_VALUE_2 = "09/12/2026"
        private const val TEST_VALUE_3 = "2026/12/09"
        private const val TEST_VALUE_WRONG =  "99/99/0020"
    }
    //endregion

    //region - Tests
    @Test
    fun test_format_pattern() {
        // Assert
        assertEquals(DateRangeFormat.MM_DD_YYYY.formatPattern, "##/##/####")
        assertEquals(DateRangeFormat.DD_MM_YYYY.formatPattern, "##/##/####")
        assertEquals(DateRangeFormat.YYYY_MM_DD.formatPattern, "####/##/##")
    }

    @Test
    fun test_size() {
        // Assert
        assertEquals(DateRangeFormat.MM_DD_YYYY.size, 10)
        assertEquals(DateRangeFormat.DD_MM_YYYY.size, 10)
        assertEquals(DateRangeFormat.YYYY_MM_DD.size, 10)
    }

    @Test
    fun test_MM_DD_YYYY_date_from_string() {
        // Arrange
        val pattern = "MM/dd/yyyy"
        val sDateFormat = SimpleDateFormat(pattern, Locale.US)
        sDateFormat.isLenient = false
        // Assert
        assertEquals(DateRangeFormat.MM_DD_YYYY.dateFromString(TEST_VALUE_1), sDateFormat.parse(TEST_VALUE_1))
        assertNull(DateRangeFormat.MM_DD_YYYY.dateFromString(TEST_VALUE_WRONG))
        assertNull(DateRangeFormat.MM_DD_YYYY.dateFromString(null))
        assertNull(DateRangeFormat.MM_DD_YYYY.dateFromString(""))
    }

    @Test
    fun test_DD_MM_YYYY_date_from_string() {
        // Arrange
        val pattern = "dd/MM/yyyy"
        val sDateFormat = SimpleDateFormat(pattern, Locale.US)
        sDateFormat.isLenient = false
        // Assert
        assertEquals(DateRangeFormat.DD_MM_YYYY.dateFromString(TEST_VALUE_2), sDateFormat.parse(TEST_VALUE_2))
        assertNull(DateRangeFormat.DD_MM_YYYY.dateFromString(TEST_VALUE_WRONG))
        assertNull(DateRangeFormat.DD_MM_YYYY.dateFromString(null))
        assertNull(DateRangeFormat.DD_MM_YYYY.dateFromString(""))
    }

    @Test
    fun test_YYYY_MM_DD_date_from_string() {
        // Arrange
        val pattern = "yyyy/MM/dd"
        val sDateFormat = SimpleDateFormat(pattern, Locale.US)
        sDateFormat.isLenient = false
        // Assert
        assertEquals(DateRangeFormat.YYYY_MM_DD.dateFromString(TEST_VALUE_3), sDateFormat.parse(TEST_VALUE_3))
        assertNull(DateRangeFormat.YYYY_MM_DD.dateFromString(TEST_VALUE_WRONG))
        assertNull(DateRangeFormat.YYYY_MM_DD.dateFromString(null))
        assertNull(DateRangeFormat.YYYY_MM_DD.dateFromString(""))
    }

    @Test
    fun test_parse_pattern_to_date_format() {
        // Assert MM/dd/yyyy
        assertEquals(DateRangeFormat.parsePatternToDateFormat("MM/dd/yyyy"), DateRangeFormat.MM_DD_YYYY)
        assertEquals(DateRangeFormat.parsePatternToDateFormat("MM-dd-yyyy"), DateRangeFormat.MM_DD_YYYY)
        assertEquals(DateRangeFormat.parsePatternToDateFormat("MM_dd_yyyy"), DateRangeFormat.MM_DD_YYYY)
        assertEquals(DateRangeFormat.parsePatternToDateFormat("MM_dd/yyyy"), DateRangeFormat.MM_DD_YYYY)
        assertNull(DateRangeFormat.parsePatternToDateFormat("MM/dd_yyyy"))
        assertNull(DateRangeFormat.parsePatternToDateFormat("MM/DD/YYYY"))
        assertNull(DateRangeFormat.parsePatternToDateFormat(""))
        assertNull(DateRangeFormat.parsePatternToDateFormat(null))
        // Assert dd/MM/yyyy
        assertEquals(DateRangeFormat.parsePatternToDateFormat("dd/MM/yyyy"), DateRangeFormat.DD_MM_YYYY)
        assertEquals(DateRangeFormat.parsePatternToDateFormat("dd-MM-yyyy"), DateRangeFormat.DD_MM_YYYY)
        assertEquals(DateRangeFormat.parsePatternToDateFormat("dd_MM_yyyy"), DateRangeFormat.DD_MM_YYYY)
        assertEquals(DateRangeFormat.parsePatternToDateFormat("dd_MM/yyyy"), DateRangeFormat.DD_MM_YYYY)
        assertNull(DateRangeFormat.parsePatternToDateFormat("dd/MM_yyyy"))
        assertNull(DateRangeFormat.parsePatternToDateFormat("DD/MM/YYYY"))
        assertNull(DateRangeFormat.parsePatternToDateFormat(""))
        assertNull(DateRangeFormat.parsePatternToDateFormat(null))
        // Assert yyyy/MM/dd
        assertEquals(DateRangeFormat.parsePatternToDateFormat("yyyy/MM/dd"), DateRangeFormat.YYYY_MM_DD)
        assertEquals(DateRangeFormat.parsePatternToDateFormat("yyyy-MM-dd"), DateRangeFormat.YYYY_MM_DD)
        assertEquals(DateRangeFormat.parsePatternToDateFormat("yyyy_MM_dd"), DateRangeFormat.YYYY_MM_DD)
        assertEquals(DateRangeFormat.parsePatternToDateFormat("yyyy_MM/dd"), DateRangeFormat.YYYY_MM_DD)
        assertNull(DateRangeFormat.parsePatternToDateFormat("yyyy/dd_MM"))
        assertNull(DateRangeFormat.parsePatternToDateFormat("YYYY/MM/DD"))
        assertNull(DateRangeFormat.parsePatternToDateFormat(""))
        assertNull(DateRangeFormat.parsePatternToDateFormat(null))
    }
    //endregion
}