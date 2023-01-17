package com.verygoodsecurity.vgscollect.core.api.analityc.action

import org.junit.Assert.*
import org.junit.Test

class ResponseActionTest {

    @Test
    fun getAttributes_typoAddedByDefault() {
        // Arrange
        val target = ResponseAction(emptyMap())
        // Act
        val result = target.getAttributes()["type"]
        // Assert
        assertEquals("Submit", result)
    }

    @Test
    fun getAttributes_customParamAndTypeAdded() {
        // Arrange
        val target = ResponseAction(mapOf("custom_param_name" to "custom_param_value"))
        // Act
        val type = target.getAttributes()["type"]
        val customParam = target.getAttributes()["custom_param_name"]
        // Assert
        assertEquals("Submit", type)
        assertEquals("custom_param_value", customParam)
    }
}