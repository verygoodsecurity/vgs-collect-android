package com.verygoodsecurity.vgscollect.view.card.validation

import com.verygoodsecurity.vgscollect.view.card.validation.rules.ABARoutingNumberRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ABARoutingNumberRuleTest {

    private val rule = ABARoutingNumberRule.ValidationBuilder()
        .setErrorMsg("Invalid ABA routing number")
        .build()

    @Test
    fun `build with aba validator should create correct ABARoutingNumberValidator`() {
        // Arrange
        val validAba = "122100024"
        val invalidAba = "123456789"

        // Act & Assert
        assertNotNull("ABA validator should not be null", rule.aba)
        assertTrue(
            "ABARoutingNumberValidator should be valid for a correct ABA number",
            rule.aba!!.isValid(validAba)
        )
        assertFalse(
            "ABARoutingNumberValidator should be invalid for an incorrect ABA number",
            rule.aba!!.isValid(invalidAba)
        )
    }

    @Test
    fun test_error_message() {
        assertEquals("Invalid ABA routing number", rule.aba?.errorMsg)
    }
}