package com.verygoodsecurity.vgscollect.card.validator.luhn

import com.verygoodsecurity.vgscollect.view.card.validation.card.brand.LuhnCheckSumDelegate
import org.junit.Assert
import org.junit.Test

class EloDelegateTest {

    @Test
    fun detectElo1() {
        val elo = LuhnCheckSumDelegate()

        val state = elo.isValid("5066991111111118")

        Assert.assertTrue(state)
    }

    @Test
    fun detectElo2() {
        val elo = LuhnCheckSumDelegate()

        val state = elo.isValid("6362970000457013")

        Assert.assertTrue(state)
    }

    @Test
    fun detectElo3() {
        val elo = LuhnCheckSumDelegate()

        val state = elo.isValid("5067310000000010")

        Assert.assertTrue(state)
    }

    @Test
    fun detectElo4() {
        val elo = LuhnCheckSumDelegate()

        val state = elo.isValid("5067312520593847")

        Assert.assertTrue(state)
    }
}