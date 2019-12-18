package com.verygoodsecurity.vgscollect.card.validator.luhn

import com.verygoodsecurity.vgscollect.view.card.validation.card.brand.MastercardDelegate
import org.junit.Assert.assertTrue
import org.junit.Test

class MastercardDelegateTest {

    @Test
    fun detectMastercard16() {
        val mastercard16 = MastercardDelegate()

        val state = mastercard16.isValid("5555555555555555")

        assertTrue(state)
    }
}