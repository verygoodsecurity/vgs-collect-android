package com.verygoodsecurity.vgscollect.card.validator.luhn

import com.verygoodsecurity.vgscollect.view.card.validation.payment.brand.LuhnCheckSumValidator
import org.junit.Assert
import org.junit.Test

class VisaDelegateTest {

    @Test
    fun detectVisa1() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4111111111111111")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa2() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4007000000027")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa3() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4012888818888")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa4() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4005519200000004")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa5() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4009348888881881")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa6() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4012000033330026")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa7() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4012000077777777")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa8() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4012888888881881")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa9() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4217651111111119")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa10() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4500600000000061")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa11() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4444333322221111")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa12() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4119862760338320")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa13() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4012001038443335")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa14() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4149011500000147")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa15() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4000007000000031")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa16() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4462030000000000")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa17() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4200000000000000")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa18() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4711100000000000")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa19() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4012001037461114")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa20() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4012001036853337")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa21() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4012001037484447")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa22() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4012001036273338")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa23() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4263970000005262")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa24() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4484070000000000")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa25() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4917610000000000003")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa26() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4911830000000")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa27() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4917610000000000")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa28() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4003830171874018")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa29() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4012001036983332")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa30() {
        val visa13 = LuhnCheckSumValidator()

        val state = visa13.isValid("4012001038488884")
        Assert.assertTrue(state)
    }
}