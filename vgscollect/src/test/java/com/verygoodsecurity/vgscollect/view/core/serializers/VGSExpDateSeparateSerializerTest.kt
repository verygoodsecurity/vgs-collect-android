package com.verygoodsecurity.vgscollect.view.core.serializers

import org.junit.Assert.assertEquals
import org.junit.Test

class VGSExpDateSeparateSerializerTest {

    @Test
    fun serialize_mmYYYY_correctReturned() {
        // Arrange
        val params = VGSExpDateSeparateSerializer.Params("09/25", "MM/yy")
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
}