package com.verygoodsecurity.vgscollect.card.validator.rule

import com.verygoodsecurity.vgscollect.view.card.validation.rules.PersonNameRule
import org.junit.Assert
import org.junit.Test

class PersonNameRuleTest {

    @Test
    fun test_create_default_rule() {
        val rule = PersonNameRule.ValidationBuilder()
            .build()
        Assert.assertEquals(null, rule.regex)
        Assert.assertArrayEquals(null, rule.length)
    }

    @Test
    fun test_min_length_rule() {
        val rule = PersonNameRule.ValidationBuilder()
            .setAllowableMinLength(4)
            .build()
        Assert.assertEquals(null, rule.regex)
        Assert.assertArrayEquals((4..256).toList().toTypedArray(), rule.length)
    }

    @Test
    fun test_max_length_rule() {
        val rule = PersonNameRule.ValidationBuilder()
            .setAllowableMaxLength(4)
            .build()
        Assert.assertEquals(null, rule.regex)
        Assert.assertArrayEquals(arrayOf(1, 2, 3, 4), rule.length)
    }

    @Test
    fun test_min_max_length_rule() {
        val rule = PersonNameRule.ValidationBuilder()
            .setAllowableMinLength(2)
            .setAllowableMaxLength(4)
            .build()
        Assert.assertEquals(null, rule.regex)
        Assert.assertArrayEquals(arrayOf(2, 3, 4), rule.length)
    }

    @Test
    fun test_4_rule() {
        val rule = PersonNameRule.ValidationBuilder()
            .setAllowableMaxLength(3)
            .setAllowableMinLength(6)
            .build()
        Assert.assertEquals(null, rule.regex)
        Assert.assertArrayEquals(arrayOf(3), rule.length)
    }

    @Test
    fun test_regex_rule() {
        val rule = PersonNameRule.ValidationBuilder()
            .setRegex("r")
            .build()
        Assert.assertEquals("r", rule.regex)
        Assert.assertArrayEquals(null, rule.length)
    }

    @Test
    fun test_regex_min_max_rule() {
        val rule = PersonNameRule.ValidationBuilder()
            .setRegex("r")
            .setAllowableMaxLength(2000)
            .setAllowableMinLength(4)
            .build()
        Assert.assertEquals("r", rule.regex)
        Assert.assertArrayEquals((4..2000).toList().toTypedArray(), rule.length)
    }

}