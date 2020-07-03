package com.verygoodsecurity.vgscollect.card.validator.luhn

import com.verygoodsecurity.vgscollect.view.card.validation.payment.brand.LuhnCheckSumValidator
import org.junit.Assert.assertTrue
import org.junit.Test

class CardBrandDelegateTest {

    @Test
    fun detectAmex1() {
        val amex = LuhnCheckSumValidator()

        val state = amex.isValid("370000000000002")

        assertTrue(state)
    }

    @Test
    fun detectAmex2() {
        val amex = LuhnCheckSumValidator()

        val state = amex.isValid("378282246310005")

        assertTrue(state)
    }

    @Test
    fun detectAmex3() {
        val amex = LuhnCheckSumValidator()

        val state = amex.isValid("371449635398431")

        assertTrue(state)
    }

    @Test
    fun detectAmex4() {
        val amex = LuhnCheckSumValidator()

        val state = amex.isValid("378734493671000")

        assertTrue(state)
    }

    @Test
    fun detectAmex5() {
        val amex = LuhnCheckSumValidator()

        val state = amex.isValid("374111111111111")

        assertTrue(state)
    }

    @Test
    fun detectAmex6() {
        val amex = LuhnCheckSumValidator()

        val state = amex.isValid("373953192351004")

        assertTrue(state)
    }

    @Test
    fun detectAmex7() {
        val amex = LuhnCheckSumValidator()

        val state = amex.isValid("346018484777573")

        assertTrue(state)
    }

    @Test
    fun detectAmex8() {
        val amex = LuhnCheckSumValidator()

        val state = amex.isValid("374101000000608")

        assertTrue(state)
    }

    @Test
    fun detectAmex9() {
        val amex = LuhnCheckSumValidator()

        val state = amex.isValid("376525000000010")

        assertTrue(state)
    }

    @Test
    fun detectAmex10() {
        val amex = LuhnCheckSumValidator()

        val state = amex.isValid("375425000000907")

        assertTrue(state)
    }

    @Test
    fun detectAmex11() {
        val amex = LuhnCheckSumValidator()

        val state = amex.isValid("343452000000306")

        assertTrue(state)
    }

    @Test
    fun detectAmex12() {
        val amex = LuhnCheckSumValidator()

        val state = amex.isValid("372349000000852")

        assertTrue(state)
    }
}