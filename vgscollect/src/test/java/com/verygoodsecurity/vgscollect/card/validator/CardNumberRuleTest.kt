package com.verygoodsecurity.vgscollect.card.validator

import com.verygoodsecurity.vgscollect.view.card.validation.bank.ChecksumAlgorithm
import com.verygoodsecurity.vgscollect.view.card.validation.bank.Rule
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class CardNumberRuleTest {

    @Test
    fun test_create_default_rule() {
        val rule = Rule.RuleBuilder()
            .build()
        assertEquals(null, rule.algorithm)
        assertArrayEquals(null, rule.length)
    }

    @Test
    fun test_checkSum_none_rule() {
        val rule = Rule.RuleBuilder()
            .setAlgorithm(ChecksumAlgorithm.NONE)
            .build()
        assertEquals(ChecksumAlgorithm.NONE, rule.algorithm)
        assertArrayEquals(null, rule.length)
    }

    @Test
    fun test_checkSum_any_rule() {
        val rule = Rule.RuleBuilder()
            .setAlgorithm(ChecksumAlgorithm.ANY)
            .build()
        assertEquals(ChecksumAlgorithm.ANY, rule.algorithm)
        assertArrayEquals(null, rule.length)
    }

    @Test
    fun test_checkSum_luhn_rule() {
        val rule = Rule.RuleBuilder()
            .setAlgorithm(ChecksumAlgorithm.LUHN)
            .build()
        assertEquals(ChecksumAlgorithm.LUHN, rule.algorithm)
        assertArrayEquals(null, rule.length)
    }

    @Test
    fun test_length_rule() {
        val rule = Rule.RuleBuilder()
            .setLength(arrayOf(4,12,16))
            .build()
        assertEquals(null, rule.algorithm)
        assertArrayEquals(arrayOf(4,12,16), rule.length)
    }

    @Test
    fun test_min_length_rule() {
        val rule = Rule.RuleBuilder()
            .setMinLength(16)
            .build()
        assertEquals(null, rule.algorithm)
        assertArrayEquals(arrayOf(16,17,18,19), rule.length)
    }

    @Test
    fun test_max_length_rule() {
        val rule = Rule.RuleBuilder()
            .setMaxLength(16)
            .build()
        assertEquals(null, rule.algorithm)
        assertArrayEquals(arrayOf(13,14,15,16), rule.length)
    }

    @Test
    fun test_min_max_length_rule() {
        val rule = Rule.RuleBuilder()
            .setMinLength(16)
            .setMaxLength(19)
            .build()
        assertEquals(null, rule.algorithm)
        assertArrayEquals(arrayOf(16,17,18,19), rule.length)
    }

    @Test
    fun test_max_min_length_rule() {
        val rule = Rule.RuleBuilder()
            .setMaxLength(19)
            .setMinLength(16)
            .build()
        assertEquals(null, rule.algorithm)
        assertArrayEquals(arrayOf(16,17,18,19), rule.length)
    }

    @Test
    fun test_2_rule() {
        val rule = Rule.RuleBuilder()
            .setMaxLength(19)
            .setMinLength(16)
            .setLength(arrayOf(12,15,19))
            .build()
        assertEquals(null, rule.algorithm)
        assertArrayEquals(arrayOf(12,15,19), rule.length)
    }

    @Test
    fun test_3_rule() {
        val rule = Rule.RuleBuilder()
            .setLength(arrayOf(12,15,19))
            .setMaxLength(19)
            .setMinLength(16)
            .build()
        assertEquals(null, rule.algorithm)
        assertArrayEquals(arrayOf(12,15,19), rule.length)
    }

    @Test
    fun test_4_rule() {
        val rule = Rule.RuleBuilder()
            .setMaxLength(13)
            .setMinLength(16)
            .build()
        assertEquals(null, rule.algorithm)
        assertArrayEquals(arrayOf(13), rule.length)
    }

    @Test
    fun test_5_rule() {
        val rule = Rule.RuleBuilder()
            .setMinLength(16)
            .setMaxLength(13)
            .build()
        assertEquals(null, rule.algorithm)
        assertArrayEquals(arrayOf(13), rule.length)
    }

}