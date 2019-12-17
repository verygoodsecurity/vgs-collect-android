package com.verygoodsecurity.vgscollect.card.validator.luhn

import com.verygoodsecurity.vgscollect.view.card.validation.card.brand.AmexDelegate
import org.junit.Assert.assertTrue
import org.junit.Test

class AmexDelegateTest {

    @Test
    fun detectAmex15() {
        val amex = AmexDelegate()

        val state = amex.isValid("378282246310005")

        assertTrue(state)
    }
}