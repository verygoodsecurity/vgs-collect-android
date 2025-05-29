package com.verygoodsecurity.vgscollect.view.core.serializers

import org.junit.Assert
import org.junit.Test

class VGSDateRangeSeparateSerializerTest {

    @Test
    fun serialize_M_D_YYYY_correctReturned() {
        // Arrange
        val serializer = VGSDateRangeSeparateSerializer("day", "month", "year")
        val expectedResult = listOf(
            "day" to "1",
            "month" to "9",
            "year" to "2025"
        )
        // Act
        val result = serializer.serialize("9/1/2025", "M/d/yyyy")
        // Assert
        Assert.assertEquals(expectedResult, result)
    }

    @Test
    fun serialize_MM_DD_YYYY_correctReturned() {
        // Arrange
        val serializer = VGSDateRangeSeparateSerializer("day", "month", "year")
        val expectedResult = listOf(
            "day" to "01",
            "month" to "09",
            "year" to "2025"
        )
        // Act
        val result = serializer.serialize("9/1/2025", "MM/dd/yyyy")
        // Assert
        Assert.assertEquals(expectedResult, result)
    }

    @Test
    fun serialize_MM_D_YYYY_correctReturned() {
        // Arrange
        val serializer = VGSDateRangeSeparateSerializer("day", "month", "year")
        val expectedResult = listOf(
            "day" to "1",
            "month" to "09",
            "year" to "2025"
        )
        // Act
        val result = serializer.serialize("9/1/2025", "MM/d/yyyy")
        // Assert
        Assert.assertEquals(expectedResult, result)
    }

    @Test
    fun serialize_MMM_DD_YYYY_correctReturned() {
        // Arrange
        val serializer = VGSDateRangeSeparateSerializer("day", "month", "year")
        val expectedResult = listOf(
            "day" to "23",
            "month" to "Sep",
            "year" to "2025"
        )
        // Act
        val result = serializer.serialize("Sep/23/2025", "MMM/dd/yyyy")
        // Assert
        Assert.assertEquals(expectedResult, result)
    }

    @Test
    fun serialize_MMM_D_YYYY_correctReturned() {
        // Arrange
        val serializer = VGSDateRangeSeparateSerializer("day", "month", "year")
        val expectedResult = listOf(
            "day" to "23",
            "month" to "Sep",
            "year" to "2025"
        )
        // Act
        val result = serializer.serialize("Sep/23/2025", "MMM/d/yyyy")
        // Assert
        Assert.assertEquals(expectedResult, result)
    }

    @Test
    fun serialize_MMMM_DD_YYYY_correctReturned() {
        // Arrange
        val serializer = VGSDateRangeSeparateSerializer("day", "month", "year")
        val expectedResult = listOf(
            "day" to "09",
            "month" to "September",
            "year" to "2025"
        )
        // Act
        val result = serializer.serialize("September/9/2025", "MMMM/dd/yyyy")
        // Assert
        Assert.assertEquals(expectedResult, result)
    }

    @Test
    fun serialize_MMMM_D_YYYY_correctReturned() {
        // Arrange
        val serializer = VGSDateRangeSeparateSerializer("day", "month", "year")
        val expectedResult = listOf(
            "day" to "9",
            "month" to "September",
            "year" to "2025"
        )
        // Act
        val result = serializer.serialize("September/9/2025", "MMMM/d/yyyy")
        // Assert
        Assert.assertEquals(expectedResult, result)
    }

    @Test
    fun serialize_incorrectParams_emptyReturned() {
        // Arrange
        val serializer = VGSDateRangeSeparateSerializer("day", "month", "year")
        val expectedResult = emptyList<Pair<String, String>>()
        // Act
        val result = serializer.serialize("9/40/25", "MMMM/dd/yyyy")
        // Assert
        Assert.assertEquals(expectedResult, result)
    }
}