package com.verygoodsecurity.vgscollect.core.api.analityc.action

import org.junit.Assert.*
import org.junit.Test

class AutofillActionTest {

    @Test
    fun getAttributes_typoAddedByDefault() {
        // Arrange
        val target = AutofillAction(emptyMap())
        // Act
        val result = target.getAttributes()["type"]
        // Assert
        assertEquals("Autofill", result)
    }

    @Test
    fun getAttributes_customParamAndTypeAdded() {
        // Arrange
        val target = AutofillAction(mapOf("custom_param_name" to "custom_param_value"))
        // Act
        val type = target.getAttributes()["type"]
        val customParam = target.getAttributes()["custom_param_name"]
        // Assert
        assertEquals("Autofill", type)
        assertEquals("custom_param_value", customParam)
    }
}