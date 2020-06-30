package com.verygoodsecurity.vgscollect.card.validator.luhn

import com.verygoodsecurity.vgscollect.view.card.validation.payment.brand.LuhnCheckSumValidator
import org.junit.Assert.assertTrue
import org.junit.Test

class VisaElectronDelegateTest {

    @Test
    fun detectVisa1() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4917300000000008")
        assertTrue(state)
    }

    @Test
    fun detectVisa2() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4917300800000000")
        assertTrue(state)
    }

    @Test
    fun detectVisa3() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("3333333333333000")
        assertTrue(state)
    }
}