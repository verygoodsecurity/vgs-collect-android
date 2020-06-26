package com.verygoodsecurity.vgscollect.card.validator

import com.verygoodsecurity.vgscollect.view.card.validation.bank.ChecksumAlgorithm
import com.verygoodsecurity.vgscollect.view.card.validation.bank.BankCardNumberRule
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class CardNumberRuleTest {

    @Test
    fun test_create_default_rule() {
        val rule = BankCardNumberRule.ValidationRuleBuilder()
            .build()
        assertEquals(null, rule.algorithm)
        assertArrayEquals(null, rule.length)
    }

    @Test
    fun test_checkSum_none_rule() {
        val rule = BankCardNumberRule.ValidationRuleBuilder()
            .setAlgorithm(ChecksumAlgorithm.NONE)
            .build()
        assertEquals(ChecksumAlgorithm.NONE, rule.algorithm)
        assertArrayEquals(null, rule.length)
    }

    @Test
    fun test_checkSum_any_rule() {
        val rule = BankCardNumberRule.ValidationRuleBuilder()
            .setAlgorithm(ChecksumAlgorithm.ANY)
            .build()
        assertEquals(ChecksumAlgorithm.ANY, rule.algorithm)
        assertArrayEquals(null, rule.length)
    }

    @Test
    fun test_checkSum_luhn_rule() {
        val rule = BankCardNumberRule.ValidationRuleBuilder()
            .setAlgorithm(ChecksumAlgorithm.LUHN)
            .build()
        assertEquals(ChecksumAlgorithm.LUHN, rule.algorithm)
        assertArrayEquals(null, rule.length)
    }

    @Test
    fun test_length_rule() {
        val rule = BankCardNumberRule.ValidationRuleBuilder()
            .setAllowableNumberLength(arrayOf(4,12,16))
            .build()
        assertEquals(null, rule.algorithm)
        assertArrayEquals(arrayOf(4,12,16), rule.length)
    }

    @Test
    fun test_min_length_rule() {
        val rule = BankCardNumberRule.ValidationRuleBuilder()
            .setAllowableMinLength(16)
            .build()
        assertEquals(null, rule.algorithm)
        assertArrayEquals(arrayOf(16,17,18,19), rule.length)
    }

    @Test
    fun test_max_length_rule() {
        val rule = BankCardNumberRule.ValidationRuleBuilder()
            .setAllowableMaxLength(16)
            .build()
        assertEquals(null, rule.algorithm)
        assertArrayEquals(arrayOf(13,14,15,16), rule.length)
    }

    @Test
    fun test_min_max_length_rule() {
        val rule = BankCardNumberRule.ValidationRuleBuilder()
            .setAllowableMinLength(16)
            .setAllowableMaxLength(19)
            .build()
        assertEquals(null, rule.algorithm)
        assertArrayEquals(arrayOf(16,17,18,19), rule.length)
    }

    @Test
    fun test_max_min_length_rule() {
        val rule = BankCardNumberRule.ValidationRuleBuilder()
            .setAllowableMaxLength(19)
            .setAllowableMinLength(16)
            .build()
        assertEquals(null, rule.algorithm)
        assertArrayEquals(arrayOf(16,17,18,19), rule.length)
    }

    @Test
    fun test_2_rule() {
        val rule = BankCardNumberRule.ValidationRuleBuilder()
            .setAllowableMaxLength(19)
            .setAllowableMinLength(16)
            .setAllowableNumberLength(arrayOf(12,15,19))
            .build()
        assertEquals(null, rule.algorithm)
        assertArrayEquals(arrayOf(12,15,19), rule.length)
    }

    @Test
    fun test_3_rule() {
        val rule = BankCardNumberRule.ValidationRuleBuilder()
            .setAllowableNumberLength(arrayOf(12,15,19))
            .setAllowableMaxLength(19)
            .setAllowableMinLength(16)
            .build()
        assertEquals(null, rule.algorithm)
        assertArrayEquals(arrayOf(12,15,19), rule.length)
    }

    @Test
    fun test_4_rule() {
        val rule = BankCardNumberRule.ValidationRuleBuilder()
            .setAllowableMaxLength(13)
            .setAllowableMinLength(16)
            .build()
        assertEquals(null, rule.algorithm)
        assertArrayEquals(arrayOf(13), rule.length)
    }

    @Test
    fun test_5_rule() {
        val rule = BankCardNumberRule.ValidationRuleBuilder()
            .setAllowableMinLength(16)
            .setAllowableMaxLength(13)
            .build()
        assertEquals(null, rule.algorithm)
        assertArrayEquals(arrayOf(13), rule.length)
    }

}