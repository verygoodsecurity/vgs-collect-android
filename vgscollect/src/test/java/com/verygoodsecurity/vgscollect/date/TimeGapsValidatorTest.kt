package com.verygoodsecurity.vgscollect.date

import com.verygoodsecurity.vgscollect.view.date.validation.TimeGapsValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TimeGapsValidatorTest {

    @Test
    fun test_positive_format_dd_MM_yyyy() {
        val validator = TimeGapsValidator("dd/MM/yyyy")

        assertTrue(validator.isValid("21/03/2021"))
        assertTrue(validator.isValid("24/09/1990"))
    }

    @Test
    fun test_positive_format_MM_yyyy() {
        val validator = TimeGapsValidator("MM/yyyy")

        assertTrue(validator.isValid("03/2021"))
        assertTrue(validator.isValid("09/1990"))
    }

    @Test
    fun test_positive_format_MM_yy() {
        val validator = TimeGapsValidator("MM/yy")

        assertTrue(validator.isValid("03/21"))
        assertTrue(validator.isValid("09/25"))
    }

    @Test
    fun test_positive_format_yyyy_MM_dd() {
        val validator = TimeGapsValidator("yyyy-MM-dd")

        assertTrue(validator.isValid("2025-09-24"))
        assertTrue(validator.isValid("1990-09-12"))
    }

    @Test
    fun test_negative_format_dd_yy_yyyy() {
        val validator = TimeGapsValidator("dd/MM/yyyy")

        assertFalse(validator.isValid("12/31/2021"))
        assertFalse(validator.isValid("aaa"))
        assertFalse(validator.isValid("12.03.2021"))
        assertFalse(validator.isValid("12.03/2021"))
    }

    @Test
    fun test_negative_format_MM_yyyy() {
        val validator = TimeGapsValidator("MM/yyyy")

        assertFalse(validator.isValid("03-2021"))
        assertFalse(validator.isValid("29/1990"))
        assertFalse(validator.isValid("29/25"))
    }

    @Test
    fun test_negative_format_MM_yy() {
        val validator = TimeGapsValidator("MM/yy")

        assertFalse(validator.isValid("23/21"))
        assertFalse(validator.isValid("09-25"))
    }

    @Test
    fun test_negative_format_yyyy_MM_dd() {
        val validator = TimeGapsValidator("yyyy-MM-dd")

        assertFalse(validator.isValid("2021/09/24"))
        assertFalse(validator.isValid("1990-29-12"))
    }

    @Test
    fun test_positive_format_yyyy_MM_dd_minDate() {
        val validator = TimeGapsValidator("yyyy-MM-dd", System.currentTimeMillis())

        assertTrue(validator.isValid("2021-09-24"))
        assertTrue(validator.isValid("2039-12-01"))
    }

    @Test
    fun test_negative_format_yyyy_MM_dd_minDate() {
        val validator = TimeGapsValidator("yyyy-MM-dd", System.currentTimeMillis())

        assertFalse(validator.isValid("1990-09-24"))
        assertFalse(validator.isValid("2019-12-01"))
    }

    @Test
    fun test_positive_format_yyyy_MM_dd_maxDate() {
        val validator = TimeGapsValidator("yyyy-MM-dd",  maxDate = 2212471406000)

        assertTrue(validator.isValid("2021-09-24"))
        assertTrue(validator.isValid("2039-12-01"))
    }

    @Test
    fun test_negative_format_yyyy_MM_dd_maxDate() {
        val validator = TimeGapsValidator("yyyy-MM-dd", maxDate = System.currentTimeMillis())

        assertFalse(validator.isValid("2031-09-24"))
        assertFalse(validator.isValid("2027-12-01"))
    }


    @Test
    fun test_positive_format_yyyy_MM_dd_maxDate_and_minDate() {
        val validator = TimeGapsValidator("yyyy-MM-dd",  System.currentTimeMillis(), 2212471406000)

        assertTrue(validator.isValid("2021-09-24"))
        assertTrue(validator.isValid("2039-12-01"))
    }

    @Test
    fun test_negative_format_yyyy_MM_dd_maxDate_and_minDate() {
        val validator = TimeGapsValidator("yyyy-MM-dd",  System.currentTimeMillis(), 2212471406000)

        assertFalse(validator.isValid("2019-09-24"))
        assertFalse(validator.isValid("2042-12-01"))
    }
}