package com.verygoodsecurity.vgscollect.card.validator

import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm
import com.verygoodsecurity.vgscollect.view.card.validation.CheckSumValidator
import com.verygoodsecurity.vgscollect.view.card.validation.CompositeValidator
import com.verygoodsecurity.vgscollect.view.card.validation.LengthMatchValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CardNumberValidatorTest {
    private val validator = CompositeValidator()

    @Before
    fun clearAll() {
        validator.clearRules()
    }

    @Test
    fun test_without_rules() {
        assertFalse(validator.validate("1234567890"))
        assertFalse(validator.validate("1"))
        assertFalse(validator.validate(""))
        assertFalse(validator.validate("411111111111111"))
    }

    @Test
    fun test_length_rule() {
        validator.addRule(LengthMatchValidator(arrayOf(16)))

        assertFalse(validator.validate("411111111111111"))
        assertTrue(validator.validate("4111111111111118"))
        assertFalse(validator.validate("4111111111111111110"))
    }

    @Test
    fun test_luhn_rule() {
        validator.addRule(CheckSumValidator(ChecksumAlgorithm.LUHN))
        assertTrue(validator.validate("4111111111111111"))
        assertFalse(validator.validate("4111111111111118"))
        assertTrue(validator.validate("4111111111111111110"))
    }

    @Test
    fun test_multiple_rules() {
        validator.addRule(LengthMatchValidator(arrayOf(16)))
        validator.addRule(CheckSumValidator(ChecksumAlgorithm.LUHN))

        assertTrue(validator.validate("4111111111111111"))
        assertFalse(validator.validate("4111111111111118"))
        assertFalse(validator.validate("4111111111111111110"))
    }

}