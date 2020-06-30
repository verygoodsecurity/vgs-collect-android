package com.verygoodsecurity.vgscollect.card.validator.luhn

import com.verygoodsecurity.vgscollect.view.card.validation.payment.brand.LuhnCheckSumValidator
import org.junit.Assert
import org.junit.Test

class DankoftDelegateTest {

    @Test
    fun detectDankoft1() {
        val dank = LuhnCheckSumValidator()

        val state = dank.isValid("5019717010103742")

        Assert.assertTrue(state)
    }

    @Test
    fun detectDankoft2() {
        val dank = LuhnCheckSumValidator()

        val state = dank.isValid("4444444444444000")

        Assert.assertTrue(state)
    }

    @Test
    fun detectDankoft3() {
        val dank = LuhnCheckSumValidator()

        val state = dank.isValid("5019555544445555")

        Assert.assertTrue(state)
    }

    @Test
    fun detectDankoft4() {
        val dank = LuhnCheckSumValidator()

        val state = dank.isValid("4571740000000002")

        Assert.assertTrue(state)
    }

    @Test
    fun detectDankoft5() {
        val dank = LuhnCheckSumValidator()

        val state = dank.isValid("5019200000000004")

        Assert.assertTrue(state)
    }

    @Test
    fun detectDankoft6() {
        val dank = LuhnCheckSumValidator()

        val state = dank.isValid("50194500000005")

        Assert.assertTrue(state)
    }
}