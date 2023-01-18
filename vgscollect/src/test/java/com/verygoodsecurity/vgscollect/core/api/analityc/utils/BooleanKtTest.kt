package com.verygoodsecurity.vgscollect.core.api.analityc.utils

import org.junit.Assert.*
import org.junit.Test

class BooleanKtTest {

    @Test
    fun toAnalyticStatus_true_okReturned() {
        // Arrange
        val target = true
        // Act
        val result = target.toAnalyticStatus()
        // Assert
        assertEquals(result, "Ok")
    }

    @Test
    fun toAnalyticStatus_false_failedReturned() {
        // Arrange
        val target = false
        // Act
        val result = target.toAnalyticStatus()
        // Assert
        assertEquals(result, "Failed")
    }
}