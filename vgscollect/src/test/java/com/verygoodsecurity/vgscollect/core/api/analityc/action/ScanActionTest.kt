package com.verygoodsecurity.vgscollect.core.api.analityc.action

import org.junit.Assert.*
import org.junit.Test

class ScanActionTest {

    @Test
    fun getAttributes_typoAddedByDefault() {
        // Arrange
        val target = ScanAction(emptyMap())
        // Act
        val result = target.getAttributes()["type"]
        // Assert
        assertEquals("Scan", result)
    }

    @Test
    fun getAttributes_customParamAndTypeAdded() {
        // Arrange
        val target = ScanAction(mapOf("custom_param_name" to "custom_param_value"))
        // Act
        val type = target.getAttributes()["type"]
        val customParam = target.getAttributes()["custom_param_name"]
        // Assert
        assertEquals("Scan", type)
        assertEquals("custom_param_value", customParam)
    }
}