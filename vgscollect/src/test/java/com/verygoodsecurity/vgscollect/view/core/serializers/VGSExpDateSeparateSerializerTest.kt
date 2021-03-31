package com.verygoodsecurity.vgscollect.view.core.serializers

import org.junit.Assert.assertEquals
import org.junit.Test

class VGSExpDateSeparateSerializerTest {

    @Test
    fun serialize_mmYYYY_correctReturned() {
        // Arrange
        val params = VGSExpDateSeparateSerializer.Params("09/25", "MM/yy")
        val serializer = VGSExpDateSeparateSerializer("month", "year", "MM", "yyyy")
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
        val params = VGSExpDateSeparateSerializer.Params("09/25", "MM/yy")
        val serializer = VGSExpDateSeparateSerializer("month", "year", "MMM", "yyyy")
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
    fun serialize_mmmmYY_correctReturned() {
        // Arrange
        val params = VGSExpDateSeparateSerializer.Params("09/25", "MM/yy")
        val serializer = VGSExpDateSeparateSerializer("month", "year", "MMMM", "yy")
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
    fun serialize_arrayKeys_correctReturned() {
        // Arrange
        val params = VGSExpDateSeparateSerializer.Params("09/25", "MM/yy")
        val serializer = VGSExpDateSeparateSerializer("data.month[0]", "data.year[1]", "MMMM", "yy")
        val expectedResult = listOf(
            "data.month[0]" to "September",
            "data.year[1]" to "25"
        )
        // Act
        val result = serializer.serialize(params)
        // Assert
        assertEquals(expectedResult, result)
    }
}