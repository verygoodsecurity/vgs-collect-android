package com.verygoodsecurity.vgscollect.card.validator.luhn

import com.verygoodsecurity.vgscollect.view.card.validation.card.brand.DiscoverDelegate
import org.junit.Assert.assertTrue
import org.junit.Test

class DiscoverDelegateTest {

    @Test
    fun detectDiscover16() {
        val discover = DiscoverDelegate()

        val state = discover.isValid("6011000000000004")

        assertTrue(state)
    }
}