package com.verygoodsecurity.vgscollect.core.api.analityc.action

import org.junit.Assert.*
import org.junit.Test

class InitActionTest {

    @Test
    fun getAttributes_typoAddedByDefault() {
        // Arrange
        val target = InitAction(emptyMap())
        // Act
        val result = target.getAttributes()["type"]
        // Assert
        assertEquals("Init", result)
    }

    @Test
    fun getAttributes_customParamAndTypeAdded() {
        // Arrange
        val target = InitAction(mapOf("custom_param_name" to "custom_param_value"))
        // Act
        val type = target.getAttributes()["type"]
        val customParam = target.getAttributes()["custom_param_name"]
        // Assert
        assertEquals("Init", type)
        assertEquals("custom_param_value", customParam)
    }
}