package com.verygoodsecurity.vgscollect.card.validator.luhn

import com.verygoodsecurity.vgscollect.view.card.validation.payment.brand.LuhnCheckSumValidator
import org.junit.Assert
import org.junit.Test

class EloDelegateTest {

    @Test
    fun detectElo1() {
        val elo = LuhnCheckSumValidator()

        val state = elo.isValid("5066991111111118")

        Assert.assertTrue(state)
    }

    @Test
    fun detectElo2() {
        val elo = LuhnCheckSumValidator()

        val state = elo.isValid("6362970000457013")

        Assert.assertTrue(state)
    }

    @Test
    fun detectElo3() {
        val elo = LuhnCheckSumValidator()

        val state = elo.isValid("5067310000000010")

        Assert.assertTrue(state)
    }

    @Test
    fun detectElo4() {
        val elo = LuhnCheckSumValidator()

        val state = elo.isValid("5067312520593847")

        Assert.assertTrue(state)
    }
}