package com.verygoodsecurity.vgscollect.card.validator

import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm
import com.verygoodsecurity.vgscollect.view.card.validation.CheckSumValidator
import com.verygoodsecurity.vgscollect.view.card.validation.CompositeValidator
import com.verygoodsecurity.vgscollect.view.card.validation.LengthValidator
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
        assertFalse(validator.isValid("1234567890"))
        assertFalse(validator.isValid("1"))
        assertFalse(validator.isValid(""))
        assertFalse(validator.isValid("411111111111111"))
    }

    @Test
    fun test_length_rule() {
        validator.addRule(LengthValidator(arrayOf(16)))

        assertFalse(validator.isValid("411111111111111"))
        assertTrue(validator.isValid("4111111111111118"))
        assertFalse(validator.isValid("4111111111111111110"))
    }

    @Test
    fun test_luhn_rule() {
        validator.addRule(CheckSumValidator(ChecksumAlgorithm.LUHN))
        assertTrue(validator.isValid("4111111111111111"))
        assertFalse(validator.isValid("4111111111111118"))
        assertTrue(validator.isValid("4111111111111111110"))
    }

    @Test
    fun test_multiple_rules() {
        validator.addRule(LengthValidator(arrayOf(16)))
        validator.addRule(CheckSumValidator(ChecksumAlgorithm.LUHN))

        assertTrue(validator.isValid("4111111111111111"))
        assertFalse(validator.isValid("4111111111111118"))
        assertFalse(validator.isValid("4111111111111111110"))
    }

}