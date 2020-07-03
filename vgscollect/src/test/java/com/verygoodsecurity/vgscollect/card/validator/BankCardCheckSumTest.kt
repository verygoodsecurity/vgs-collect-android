package com.verygoodsecurity.vgscollect.card.validator

import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm
import com.verygoodsecurity.vgscollect.view.card.validation.CheckSumValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BankCardCheckSumTest {

    @Test
    fun test_none() {
        val s = CheckSumValidator(ChecksumAlgorithm.NONE)

        assertTrue(s.isValid(""))
        assertTrue(s.isValid("1"))
        assertTrue(s.isValid("123"))
        assertTrue(s.isValid("4111111111111111"))
    }

    @Test
    fun test_any() {
        val s = CheckSumValidator(ChecksumAlgorithm.ANY)

        assertFalse(s.isValid(""))
        assertTrue(s.isValid("4111111111111111"))
    }

    @Test
    fun test_luhn() {
        val s = CheckSumValidator(ChecksumAlgorithm.LUHN)

        assertFalse(s.isValid(""))
        assertTrue(s.isValid("4111111111111111"))
    }
}