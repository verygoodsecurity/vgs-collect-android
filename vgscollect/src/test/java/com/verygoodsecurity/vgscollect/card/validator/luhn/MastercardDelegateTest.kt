package com.verygoodsecurity.vgscollect.card.validator.luhn

import com.verygoodsecurity.vgscollect.view.card.validation.payment.brand.LuhnCheckSumValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MastercardDelegateTest {

    @Test
    fun detectMastercard1() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("5555555555554444")

        assertTrue(state)
    }

    @Test
    fun detectMastercard2() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("5454545454545454")

        assertTrue(state)
    }

    @Test
    fun detectMastercard3() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("5105105105105100")

        assertTrue(state)
    }

    @Test
    fun detectMastercard4() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("5399999999999999")

        assertTrue(state)
    }

    @Test
    fun detectMastercard5() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("5232569007637831")

        assertTrue(state)
    }

    @Test
    fun detectMastercard6() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("5556011778787485")

        assertTrue(state)
    }

    @Test
    fun detectMastercard7() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("2720992593319364")

        assertTrue(state)
    }

    @Test
    fun detectMastercard8() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("2222420000001113")

        assertTrue(state)
    }

    @Test
    fun detectMastercard9() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("2222630000001125")

        assertTrue(state)
    }

    @Test
    fun detectMastercard10() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("5420923878724339")

        assertTrue(state)
    }

    @Test
    fun detectMastercard11() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("5185540810000019")

        assertTrue(state)
    }

    @Test
    fun detectMastercard12() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("5111010030175156")

        assertTrue(state)
    }

    @Test
    fun detectMastercard13() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("5200828282828210")

        assertTrue(state)
    }

    @Test
    fun detectMastercard14() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("5204230080000017")

        assertTrue(state)
    }

    @Test
    fun detectMastercard15() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("5555555555555000")

        assertFalse(state)
    }

    @Test
    fun detectMastercard16() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("5425230000004415")

        assertTrue(state)
    }

    @Test
    fun detectMastercard17() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("5114610000004778")

        assertTrue(state)
    }

    @Test
    fun detectMastercard18() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("5114630000009791")

        assertTrue(state)
    }

    @Test
    fun detectMastercard19() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("5121220000006921")

        assertTrue(state)
    }

    @Test
    fun detectMastercard20() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("5135020000005871")

        assertTrue(state)
    }

    @Test
    fun detectMastercard21() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("5100000000000131")

        assertTrue(state)
    }

    @Test
    fun detectMastercard22() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("5301250070000050")

        assertTrue(state)
    }

    @Test
    fun detectMastercard23() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("5454609899026213")

        assertTrue(state)
    }

    @Test
    fun detectMastercard24() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("5123456789012346")

        assertTrue(state)
    }

    @Test
    fun detectMastercard25() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("5133333333333338")

        assertTrue(state)
    }

    @Test
    fun detectMastercard26() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("5111111111111118")

        assertTrue(state)
    }

    @Test
    fun detectMastercard27() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("2223000000000023")

        assertTrue(state)
    }

    @Test
    fun detectMastercard28() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("5413000000000000")

        assertTrue(state)
    }

    @Test
    fun detectMastercard29() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("5404000000000068")

        assertTrue(state)
    }

    @Test
    fun detectMastercard30() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("5404000000000084")

        assertTrue(state)
    }

    @Test
    fun detectMastercard31() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("5404000000000043")

        assertTrue(state)
    }

    @Test
    fun detectMastercard32() {
        val mastercard16 = LuhnCheckSumValidator()

        val state = mastercard16.isValid("5496198584584769")

        assertTrue(state)
    }
}