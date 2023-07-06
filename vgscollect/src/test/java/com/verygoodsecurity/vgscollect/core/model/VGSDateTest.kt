package com.verygoodsecurity.vgscollect.core.model

import com.verygoodsecurity.vgscollect.view.date.DateRangeFormatTest
import org.junit.Assert.*
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

class VGSDateTest {

    @Test
    fun test_create() {
        // Assert
        assertNotNull(VGSDate.create(3, 12, 2020))
        assertEquals(VGSDate.create(3, 12, 2020)?.timeInMillis, timeInMillis(3, 12, 2020))
        assertEquals(VGSDate.create(24, 10, 2025)?.timeInMillis, timeInMillis(24, 10, 2025))
        assertNull(VGSDate.create(3, 15, 2023))
        assertNull(VGSDate.create(40, 10, 2023))
    }

    @Test
    fun test_create_failure() {
        //VGSDate.create()
    }

    private fun timeInMillis(day: Int, month: Int, year: Int): Long = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.MONTH, month - 1)
        set(Calendar.YEAR, year)
        set(Calendar.HOUR_OF_DAY, getActualMaximum(Calendar.HOUR_OF_DAY))
        set(Calendar.MINUTE, getActualMaximum(Calendar.MINUTE))
        set(Calendar.SECOND, getActualMaximum(Calendar.SECOND))
        set(Calendar.MILLISECOND, getActualMaximum(Calendar.MILLISECOND))
    }.timeInMillis
}