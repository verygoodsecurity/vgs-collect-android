package com.verygoodsecurity.vgscollect.core.api.analityc.action

import org.junit.Assert.*
import org.junit.Test

class HostNameValidationActionTest {

    @Test
    fun getAttributes_typoAddedByDefault() {
        // Arrange
        val target = HostNameValidationAction(emptyMap())
        // Act
        val result = target.getAttributes()["type"]
        // Assert
        assertEquals("HostNameValidation", result)
    }

    @Test
    fun getAttributes_customParamAndTypeAdded() {
        // Arrange
        val target = HostNameValidationAction(mapOf("custom_param_name" to "custom_param_value"))
        // Act
        val type = target.getAttributes()["type"]
        val customParam = target.getAttributes()["custom_param_name"]
        // Assert
        assertEquals("HostNameValidation", type)
        assertEquals("custom_param_value", customParam)
    }
}