package com.verygoodsecurity.vgscollect.card.validator.luhn

import com.verygoodsecurity.vgscollect.view.card.validation.payment.brand.LuhnCheckSumValidator
import org.junit.Assert.assertTrue
import org.junit.Test

class DiscoverDelegateTest {

    @Test
    fun detectDiscover1() {
        val discover = LuhnCheckSumValidator()

        val state = discover.isValid("6011000000000004")

        assertTrue(state)
    }

    @Test
    fun detectDiscover2() {
        val discover = LuhnCheckSumValidator()

        val state = discover.isValid("6011111111111117")

        assertTrue(state)
    }

    @Test
    fun detectDiscover3() {
        val discover = LuhnCheckSumValidator()

        val state = discover.isValid("6011000990139424")

        assertTrue(state)
    }

    @Test
    fun detectDiscover4() {
        val discover = LuhnCheckSumValidator()

        val state = discover.isValid("6011000400000000")

        assertTrue(state)
    }

    @Test
    fun detectDiscover5() {
        val discover = LuhnCheckSumValidator()

        val state = discover.isValid("6011000000000087")

        assertTrue(state)
    }

    @Test
    fun detectDiscover6() {
        val discover = LuhnCheckSumValidator()

        val state = discover.isValid("6011000000001010")

        assertTrue(state)
    }

    @Test
    fun detectDiscover7() {
        val discover = LuhnCheckSumValidator()

        val state = discover.isValid("6011000000001028")

        assertTrue(state)
    }

    @Test
    fun detectDiscover8() {
        val discover = LuhnCheckSumValidator()

        val state = discover.isValid("6011000000001036")

        assertTrue(state)
    }

    @Test
    fun detectDiscover9() {
        val discover = LuhnCheckSumValidator()

        val state = discover.isValid("6011000000002000")

        assertTrue(state)
    }

    @Test
    fun detectDiscover10() {
        val discover = LuhnCheckSumValidator()

        val state = discover.isValid("6011000000000012")

        assertTrue(state)
    }

}