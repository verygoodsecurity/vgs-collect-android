package com.verygoodsecurity.vgscollect.card.validator.luhn

import com.verygoodsecurity.vgscollect.view.card.validation.payment.brand.LuhnCheckSumValidator
import org.junit.Assert.assertTrue
import org.junit.Test

class JCBDelegateTest {

    @Test
    fun detectJcb1() {
        val jcb16 = LuhnCheckSumValidator()

        val state = jcb16.isValid("3088000000000017")

        assertTrue(state)
    }

    @Test
    fun detectJcb2() {
        val jcb16 = LuhnCheckSumValidator()

        val state = jcb16.isValid("3530111333300000")

        assertTrue(state)
    }

    @Test
    fun detectJcb3() {
        val jcb16 = LuhnCheckSumValidator()

        val state = jcb16.isValid("3566002020360505")

        assertTrue(state)
    }

    @Test
    fun detectJcb4() {
        val jcb16 = LuhnCheckSumValidator()

        val state = jcb16.isValid("356600202036050013")

        assertTrue(state)
    }

    @Test
    fun detectJcb5() {
        val jcb16 = LuhnCheckSumValidator()

        val state = jcb16.isValid("3566111111111113")

        assertTrue(state)
    }

    @Test
    fun detectJcb6() {
        val jcb16 = LuhnCheckSumValidator()

        val state = jcb16.isValid("3566000000000000")

        assertTrue(state)
    }

    @Test
    fun detectJcb7() {
        val jcb16 = LuhnCheckSumValidator()

        val state = jcb16.isValid("3566000000001016")

        assertTrue(state)
    }

    @Test
    fun detectJcb8() {
        val jcb16 = LuhnCheckSumValidator()

        val state = jcb16.isValid("3566000000001024")

        assertTrue(state)
    }

    @Test
    fun detectJcb9() {
        val jcb16 = LuhnCheckSumValidator()

        val state = jcb16.isValid("3566000000001032")

        assertTrue(state)
    }

    @Test
    fun detectJcb10() {
        val jcb16 = LuhnCheckSumValidator()

        val state = jcb16.isValid("3566000000002006")

        assertTrue(state)
    }

    @Test
    fun detectJcb11() {
        val jcb16 = LuhnCheckSumValidator()

        val state = jcb16.isValid("3569990000000009")

        assertTrue(state)
    }

    @Test
    fun detectJcb12() {
        val jcb16 = LuhnCheckSumValidator()

        val state = jcb16.isValid("3528000700000000")

        assertTrue(state)
    }
}