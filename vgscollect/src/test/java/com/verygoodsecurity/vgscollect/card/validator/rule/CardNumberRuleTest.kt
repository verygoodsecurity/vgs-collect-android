package com.verygoodsecurity.vgscollect.card.validator.rule

import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm
import com.verygoodsecurity.vgscollect.view.card.validation.rules.PaymentCardNumberRule
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class CardNumberRuleTest {

    @Test
    fun test_create_default_rule() {
        val rule = PaymentCardNumberRule.ValidationBuilder()
            .build()
        assertEquals(null, rule.algorithm)
        assertEquals(null, rule.length)
    }

    @Test
    fun test_checkSum_none_rule() {
        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setAlgorithm(ChecksumAlgorithm.NONE)
            .build()
        assertEquals(ChecksumAlgorithm.NONE, rule.algorithm?.value)
        assertEquals(null, rule.length)
    }

    @Test
    fun test_checkSum_any_rule() {
        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setAlgorithm(ChecksumAlgorithm.ANY)
            .build()
        assertEquals(ChecksumAlgorithm.ANY, rule.algorithm?.value)
        assertEquals(null, rule.length)
    }

    @Test
    fun test_checkSum_luhn_rule() {
        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setAlgorithm(ChecksumAlgorithm.LUHN)
            .build()
        assertEquals(ChecksumAlgorithm.LUHN, rule.algorithm?.value)
        assertEquals(null, rule.length)
    }

    @Test
    fun test_length_rule() {
        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setAllowableNumberLength(arrayOf(4,12,16))
            .build()
        assertEquals(null, rule.algorithm)
        assertArrayEquals(arrayOf(4,12,16), rule.lengthMatch?.values)
    }

    @Test
    fun test_min_length_rule() {
        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setAllowableMinLength(16)
            .build()
        assertEquals(null, rule.algorithm)
        assertEquals(16, rule.length?.min)
        assertEquals(19, rule.length?.max)
    }

    @Test
    fun test_max_length_rule() {
        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setAllowableMaxLength(16)
            .build()
        assertEquals(null, rule.algorithm)
        assertEquals(16, rule.length?.max)
        assertEquals(13, rule.length?.min)
    }

    @Test
    fun test_min_max_length_rule() {
        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setAllowableMinLength(16)
            .setAllowableMaxLength(19)
            .build()
        assertEquals(null, rule.algorithm)
        assertEquals(16, rule.length?.min)
        assertEquals(19, rule.length?.max)
    }

    @Test
    fun test_2_rule() {
        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setAllowableMaxLength(19)
            .setAllowableMinLength(16)
            .setAllowableNumberLength(arrayOf(12,15,19))
            .build()
        assertEquals(null, rule.algorithm)
        assertArrayEquals(arrayOf(12,15,19), rule.lengthMatch?.values)
        assertEquals(16, rule.length?.min)
        assertEquals(19, rule.length?.max)
    }

    @Test
    fun test_3_rule() {
        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setAllowableNumberLength(arrayOf(12,15,19))
            .setAllowableMaxLength(19)
            .setAllowableMinLength(16)
            .build()
        assertEquals(null, rule.algorithm)
        assertArrayEquals(arrayOf(12,15,19), rule.lengthMatch?.values)
        assertEquals(16, rule.length?.min)
        assertEquals(19, rule.length?.max)
    }

    @Test
    fun test_regex_min_max_rule() {
        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setRegex("r")
            .setAllowableMaxLength(19)
            .setAllowableMinLength(12)
            .build()
        assertEquals("r", rule.regex?.value)
        assertEquals(12, rule.length?.min)
        assertEquals(19, rule.length?.max)
    }
}