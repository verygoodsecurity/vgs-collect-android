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
        private const val TEST_VALUE_4 = "10/26"
        private const val TEST_VALUE_WRONG =  "99/99/0020"
    }
    //endregion

    //region - Tests
    @Test
    fun test_format_pattern() {
        // Assert
        assertEquals(DateRangeFormat.MMddYYYY.formatPattern, "##/##/####")
        assertEquals(DateRangeFormat.DDmmYYYY.formatPattern, "##/##/####")
        assertEquals(DateRangeFormat.YYYYmmDD.formatPattern, "####/##/##")
        assertEquals(DateRangeFormat.MMyy.formatPattern, "##/##")
    }

    @Test
    fun test_size() {
        // Assert
        assertEquals(DateRangeFormat.MMddYYYY.size, 10)
        assertEquals(DateRangeFormat.DDmmYYYY.size, 10)
        assertEquals(DateRangeFormat.YYYYmmDD.size, 10)
        assertEquals(DateRangeFormat.MMyy.size, 5)
    }

    @Test
    fun test_MM_DD_YYYY_date_from_string() {
        // Arrange
        val pattern = "MM/dd/yyyy"
        val sDateFormat = SimpleDateFormat(pattern, Locale.US)
        sDateFormat.isLenient = false
        // Assert
        assertEquals(DateRangeFormat.MMddYYYY.dateFromString(TEST_VALUE_1), sDateFormat.parse(TEST_VALUE_1))
        assertNull(DateRangeFormat.MMddYYYY.dateFromString(TEST_VALUE_WRONG))
        assertNull(DateRangeFormat.MMddYYYY.dateFromString(null))
        assertNull(DateRangeFormat.MMddYYYY.dateFromString(""))
    }

    @Test
    fun test_DD_MM_YYYY_date_from_string() {
        // Arrange
        val pattern = "dd/MM/yyyy"
        val sDateFormat = SimpleDateFormat(pattern, Locale.US)
        sDateFormat.isLenient = false
        // Assert
        assertEquals(DateRangeFormat.DDmmYYYY.dateFromString(TEST_VALUE_2), sDateFormat.parse(TEST_VALUE_2))
        assertNull(DateRangeFormat.DDmmYYYY.dateFromString(TEST_VALUE_WRONG))
        assertNull(DateRangeFormat.DDmmYYYY.dateFromString(null))
        assertNull(DateRangeFormat.DDmmYYYY.dateFromString(""))
    }

    @Test
    fun test_YYYY_MM_DD_date_from_string() {
        // Arrange
        val pattern = "yyyy/MM/dd"
        val sDateFormat = SimpleDateFormat(pattern, Locale.US)
        sDateFormat.isLenient = false
        // Assert
        assertEquals(DateRangeFormat.YYYYmmDD.dateFromString(TEST_VALUE_3), sDateFormat.parse(TEST_VALUE_3))
        assertNull(DateRangeFormat.YYYYmmDD.dateFromString(TEST_VALUE_WRONG))
        assertNull(DateRangeFormat.YYYYmmDD.dateFromString(null))
        assertNull(DateRangeFormat.YYYYmmDD.dateFromString(""))
    }

    @Test
    fun test_MM_YY_date_from_string() {
        // Arrange
        val pattern = "MM/yy"
        val sDateFormat = SimpleDateFormat(pattern, Locale.US)
        sDateFormat.isLenient = false
        // Assert
        assertEquals(DateRangeFormat.MMyy.dateFromString(TEST_VALUE_4), sDateFormat.parse(TEST_VALUE_4))
        assertNull(DateRangeFormat.MMyy.dateFromString(TEST_VALUE_WRONG))
        assertNull(DateRangeFormat.MMyy.dateFromString(null))
        assertNull(DateRangeFormat.MMyy.dateFromString(""))
    }

    @Test
    fun test_parse_pattern_to_date_format() {
        // Assert MM/dd/yyyy
        assertEquals(DateRangeFormat.parsePatternToDateFormat("MM/dd/yyyy"), DateRangeFormat.MMddYYYY)
        assertEquals(DateRangeFormat.parsePatternToDateFormat("MM-dd-yyyy"), DateRangeFormat.MMddYYYY)
        assertEquals(DateRangeFormat.parsePatternToDateFormat("MM_dd_yyyy"), DateRangeFormat.MMddYYYY)
        assertEquals(DateRangeFormat.parsePatternToDateFormat("MM_dd/yyyy"), DateRangeFormat.MMddYYYY)
        assertNull(DateRangeFormat.parsePatternToDateFormat("MM/dd_yyyy"))
        assertNull(DateRangeFormat.parsePatternToDateFormat("MM/DD/YYYY"))
        assertNull(DateRangeFormat.parsePatternToDateFormat(""))
        assertNull(DateRangeFormat.parsePatternToDateFormat(null))
        // Assert dd/MM/yyyy
        assertEquals(DateRangeFormat.parsePatternToDateFormat("dd/MM/yyyy"), DateRangeFormat.DDmmYYYY)
        assertEquals(DateRangeFormat.parsePatternToDateFormat("dd-MM-yyyy"), DateRangeFormat.DDmmYYYY)
        assertEquals(DateRangeFormat.parsePatternToDateFormat("dd_MM_yyyy"), DateRangeFormat.DDmmYYYY)
        assertEquals(DateRangeFormat.parsePatternToDateFormat("dd_MM/yyyy"), DateRangeFormat.DDmmYYYY)
        assertNull(DateRangeFormat.parsePatternToDateFormat("dd/MM_yyyy"))
        assertNull(DateRangeFormat.parsePatternToDateFormat("DD/MM/YYYY"))
        assertNull(DateRangeFormat.parsePatternToDateFormat(""))
        assertNull(DateRangeFormat.parsePatternToDateFormat(null))
        // Assert yyyy/MM/dd
        assertEquals(DateRangeFormat.parsePatternToDateFormat("yyyy/MM/dd"), DateRangeFormat.YYYYmmDD)
        assertEquals(DateRangeFormat.parsePatternToDateFormat("yyyy-MM-dd"), DateRangeFormat.YYYYmmDD)
        assertEquals(DateRangeFormat.parsePatternToDateFormat("yyyy_MM_dd"), DateRangeFormat.YYYYmmDD)
        assertEquals(DateRangeFormat.parsePatternToDateFormat("yyyy_MM/dd"), DateRangeFormat.YYYYmmDD)
        assertNull(DateRangeFormat.parsePatternToDateFormat("yyyy/dd_MM"))
        assertNull(DateRangeFormat.parsePatternToDateFormat("YYYY/MM/DD"))
        assertNull(DateRangeFormat.parsePatternToDateFormat(""))
        assertNull(DateRangeFormat.parsePatternToDateFormat(null))
        // Assert MM/yy
        assertEquals(DateRangeFormat.parsePatternToDateFormat("MM/yy"), DateRangeFormat.MMyy)
        assertEquals(DateRangeFormat.parsePatternToDateFormat("MM-yy"), DateRangeFormat.MMyy)
        assertEquals(DateRangeFormat.parsePatternToDateFormat("MM_yy"), DateRangeFormat.MMyy)
        assertNull(DateRangeFormat.parsePatternToDateFormat(""))
        assertNull(DateRangeFormat.parsePatternToDateFormat(null))
    }
    //endregion
}