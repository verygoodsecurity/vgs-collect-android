package com.verygoodsecurity.vgscollect.card.validator.luhn

import com.verygoodsecurity.vgscollect.view.card.validation.payment.brand.LuhnCheckSumValidator
import org.junit.Assert
import org.junit.Test

class MaestroDelegateTest {

    @Test
    fun detectMaestro1() {
        val maestro = LuhnCheckSumValidator()

        val state = maestro.isValid("6771798021000008")

        Assert.assertTrue(state)
    }

    @Test
    fun detectMaestro2() {
        val maestro = LuhnCheckSumValidator()

        val state = maestro.isValid("5000550000000029")

        Assert.assertTrue(state)
    }

    @Test
    fun detectMaestro3() {
        val maestro = LuhnCheckSumValidator()

        val state = maestro.isValid("6771830999991239")

        Assert.assertTrue(state)
    }

    @Test
    fun detectMaestro4() {
        val maestro = LuhnCheckSumValidator()

        val state = maestro.isValid("6771830000000000006")

        Assert.assertTrue(state)
    }

    @Test
    fun detectMaestro5() {
        val maestro = LuhnCheckSumValidator()

        val state = maestro.isValid("6759649826438453")

        Assert.assertTrue(state)
    }

    @Test
    fun detectMaestro6() {
        val maestro = LuhnCheckSumValidator()

        val state = maestro.isValid("6759000000000000005")

        Assert.assertTrue(state)
    }

    @Test
    fun detectMaestro7() {
        val maestro = LuhnCheckSumValidator()

        val state = maestro.isValid("6799990100000000019")

        Assert.assertTrue(state)
    }

    @Test
    fun detectMaestro8() {
        val maestro = LuhnCheckSumValidator()

        val state = maestro.isValid("5001630000000002")

        Assert.assertTrue(state)
    }

    @Test
    fun detectMaestro9() {
        val maestro = LuhnCheckSumValidator()

        val state = maestro.isValid("5016590000000001")

        Assert.assertTrue(state)
    }

    @Test
    fun detectMaestro10() {
        val maestro = LuhnCheckSumValidator()

        val state = maestro.isValid("5020620000000000")

        Assert.assertTrue(state)
    }

    @Test
    fun detectMaestro11() {
        val maestro = LuhnCheckSumValidator()

        val state = maestro.isValid("5612370000000006")

        Assert.assertTrue(state)
    }
}