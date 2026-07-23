package com.verygoodsecurity.vgscollect.view.core.serializers

import org.junit.Assert.assertEquals
import org.junit.Test

class VGSExpDateSeparateSerializerTest {

    @Test
    fun serialize_mYYYY_correctReturned() {
        // Arrange
        val serializer = VGSExpDateSeparateSerializer("month", "year")
        val expectedResult = listOf(
            "month" to "9",
            "year" to "2025"
        )
        // Act
        val result = serializer.serialize("9/2025", "M/yyyy")
        // Assert
        assertEquals(expectedResult, result)
    }

    @Test
    fun serialize_mmYYYY_correctReturned() {
        // Arrange
        val serializer = VGSExpDateSeparateSerializer("month", "year")
        val expectedResult = listOf(
            "month" to "09",
            "year" to "2025"
        )
        // Act
        val result = serializer.serialize("09/2025", "MM/yyyy")
        // Assert
        assertEquals(expectedResult, result)
    }

    @Test
    fun serialize_mmmYYYY_correctReturned() {
        // Arrange
        val serializer = VGSExpDateSeparateSerializer("month", "year")
        val expectedResult = listOf(
            "month" to "Sep",
            "year" to "2025"
        )
        // Act
        val result = serializer.serialize("Sep/2025", "MMM/yyyy")
        // Assert
        assertEquals(expectedResult, result)
    }

    @Test
    fun serialize_mmmmYYYY_correctReturned() {
        // Arrange
        val serializer = VGSExpDateSeparateSerializer("month", "year")
        val expectedResult = listOf(
            "month" to "September",
            "year" to "2025"
        )
        // Act
        val result = serializer.serialize("September/2025", "MMMM/yyyy")
        // Assert
        assertEquals(expectedResult, result)
    }

    @Test
    fun serialize_mmmmYY_correctReturned() {
        // Arrange
        val serializer = VGSExpDateSeparateSerializer("month", "year")
        val expectedResult = listOf(
            "month" to "September",
            "year" to "25"
        )
        // Act
        val result = serializer.serialize("September/25", "MMMM/yy")
        // Assert
        assertEquals(expectedResult, result)
    }

    @Test
    fun serialize_incorrectParams_emptyReturned() {
        // Arrange
        val serializer = VGSExpDateSeparateSerializer("month", "year")
        val expectedResult = emptyList<Pair<String, String>>()
        // Act
        val result = serializer.serialize("9/25", "MMMM/yy")
        // Assert
        assertEquals(expectedResult, result)
    }
}