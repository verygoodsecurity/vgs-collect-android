package com.verygoodsecurity.vgscollect.core.api.analityc.action

import org.junit.Assert.assertEquals
import org.junit.Test

class InitActionTest {

    @Test
    fun getAttributes() {
        // Arrange
        val target = InitAction(fieldType = "field_type", isCompose = false)
        // Act
        val type = target.getAttributes()["type"]
        val fieldType = target.getAttributes()["field"]
        val ui = target.getAttributes()["ui"]
        // Assert
        assertEquals("Init", type)
        assertEquals("field_type", fieldType)
        assertEquals("xml", ui)
    }
}