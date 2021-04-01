package com.verygoodsecurity.vgscollect.view.core.serializers

import org.junit.Assert.assertEquals
import org.junit.Test

class VGSExpDateSeparateSerializerTest {

    @Test
    fun serialize_mYYYY_correctReturned() {
        // Arrange
        val params = VGSExpDateSeparateSerializer.Params("9/2025", "M/yyyy")
        val serializer = VGSExpDateSeparateSerializer("month", "year")
        val expectedResult = listOf(
            "month" to "9",
            "year" to "2025"
        )
        // Act
        val result = serializer.serialize(params)
        // Assert
        assertEquals(expectedResult, result)
    }

    @Test
    fun serialize_mmYYYY_correctReturned() {
        // Arrange
        val params = VGSExpDateSeparateSerializer.Params("09/2025", "MM/yyyy")
        val serializer = VGSExpDateSeparateSerializer("month", "year")
        val expectedResult = listOf(
            "month" to "09",
            "year" to "2025"
        )
        // Act
        val result = serializer.serialize(params)
        // Assert
        assertEquals(expectedResult, result)
    }

    @Test
    fun serialize_mmmYYYY_correctReturned() {
        // Arrange
        val params = VGSExpDateSeparateSerializer.Params("Sep/2025", "MMM/yyyy")
        val serializer = VGSExpDateSeparateSerializer("month", "year")
        val expectedResult = listOf(
            "month" to "Sep",
            "year" to "2025"
        )
        // Act
        val result = serializer.serialize(params)
        // Assert
        assertEquals(expectedResult, result)
    }

    @Test
    fun serialize_mmmmYYYY_correctReturned() {
        // Arrange
        val params = VGSExpDateSeparateSerializer.Params("September/2025", "MMMM/yyyy")
        val serializer = VGSExpDateSeparateSerializer("month", "year")
        val expectedResult = listOf(
            "month" to "September",
            "year" to "2025"
        )
        // Act
        val result = serializer.serialize(params)
        // Assert
        assertEquals(expectedResult, result)
    }

    @Test
    fun serialize_mmmmYY_correctReturned() {
        // Arrange
        val params = VGSExpDateSeparateSerializer.Params("September/25", "MMMM/yy")
        val serializer = VGSExpDateSeparateSerializer("month", "year")
        val expectedResult = listOf(
            "month" to "September",
            "year" to "25"
        )
        // Act
        val result = serializer.serialize(params)
        // Assert
        assertEquals(expectedResult, result)
    }

    @Test
    fun serialize_incorrectParams_emptyReturned() {
        // Arrange
        val params = VGSExpDateSeparateSerializer.Params("9/25", "MMMM/yy")
        val serializer = VGSExpDateSeparateSerializer("month", "year")
        val expectedResult = emptyList<Pair<String, String>>()
        // Act
        val result = serializer.serialize(params)
        // Assert
        assertEquals(expectedResult, result)
    }
}