package com.verygoodsecurity.vgscollect.view.card.validation

import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm
import com.verygoodsecurity.vgscollect.view.card.validation.rules.PaymentCardNumberRule
import org.junit.Assert
import org.junit.Test

class PaymentCardNumberRuleTest {

    @Test
    fun `build with Luhn algorithm should create correct CheckSumValidator`() {
        // Arrange
        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setAlgorithm(ChecksumAlgorithm.LUHN)
            .build()
        val validLuhnCard = "49927398716"
        val invalidLuhnCard = "49927398717"

        // Act & Assert
        Assert.assertNotNull("Algorithm validator should not be null", rule.algorithm)
        Assert.assertTrue(
            "CheckSumValidator should be valid for a correct Luhn number",
            rule.algorithm!!.isValid(validLuhnCard)
        )
        Assert.assertFalse(
            "CheckSumValidator should be invalid for an incorrect Luhn number",
            rule.algorithm!!.isValid(invalidLuhnCard)
        )
    }
    //endregion

    //region Regex Tests
    @Test
    fun `build with regex should create correct RegexValidator`() {
        // Arrange
        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setRegex("^4\\d{15}$") // Regex for a 16-digit Visa card
            .build()
        val visaCard = "4111222233334444"
        val mastercard = "5111222233334444"

        // Act & Assert
        Assert.assertNotNull("Regex validator should not be null", rule.regex)
        Assert.assertTrue(
            "RegexValidator should be valid for input matching the regex",
            rule.regex!!.isValid(visaCard)
        )
        Assert.assertFalse(
            "RegexValidator should be invalid for input not matching the regex",
            rule.regex!!.isValid(mastercard)
        )
    }
    //endregion

    //region Length Tests
    @Test
    fun `build with min and max length should create correct LengthValidator`() {
        // Arrange
        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setAllowableMinLength(15)
            .setAllowableMaxLength(16)
            .build()
        val inputLength14 = "12345678901234"
        val inputLength15 = "123456789012345"
        val inputLength17 = "12345678901234567"

        // Act & Assert
        Assert.assertNotNull("Length validator should not be null", rule.length)
        Assert.assertFalse(
            "LengthValidator should be invalid for length 14",
            rule.length!!.isValid(inputLength14)
        )
        Assert.assertTrue(
            "LengthValidator should be valid for length 15",
            rule.length!!.isValid(inputLength15)
        )
        Assert.assertFalse(
            "LengthValidator should be invalid for length 17",
            rule.length!!.isValid(inputLength17)
        )
    }

    @Test
    fun `build with exact lengths should create correct LengthMatchValidator`() {
        // Arrange
        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setAllowableNumberLength(arrayOf(15, 16))
            .build()
        val inputLength15 = "123456789012345"
        val inputLength17 = "12345678901234567"

        // Act & Assert
        Assert.assertNotNull("LengthMatch validator should not be null", rule.lengthMatch)
        Assert.assertTrue(
            "LengthMatchValidator should be valid for length 15",
            rule.lengthMatch!!.isValid(inputLength15)
        )
        Assert.assertFalse(
            "LengthMatchValidator should be invalid for length 17",
            rule.lengthMatch!!.isValid(inputLength17)
        )
    }
    //endregion

    //region Combined Rule Test
    @Test
    fun `build with multiple validators should configure all of them`() {
        // Arrange
        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setAlgorithm(ChecksumAlgorithm.LUHN)
            .setRegex("^4\\d{15}$")
            .setAllowableNumberLength(arrayOf(16))
            .build()

        // Correct 16-digit Visa card number that passes the Luhn check
        val validVisaLuhnCard = "4111111111111111"
        // Valid 16-digit Mastercard number. Fails regex, but passes Luhn.
        val nonVisaLuhnCard = "5454545454545454"

        // Act & Assert
        Assert.assertNotNull(rule.algorithm)
        Assert.assertNotNull(rule.regex)
        Assert.assertNotNull(rule.lengthMatch)
        Assert.assertNull(rule.length) // Length validator should be null as LengthMatch is used

        // --- Assertions for the valid card ---
        Assert.assertTrue("Luhn should be valid for the correct card", rule.algorithm!!.isValid(validVisaLuhnCard))
        Assert.assertTrue("Regex should be valid for the correct card", rule.regex!!.isValid(validVisaLuhnCard))
        Assert.assertTrue("LengthMatch should be valid for the correct card", rule.lengthMatch!!.isValid(validVisaLuhnCard))

        // --- Assertions for the card that fails the regex ---
        Assert.assertTrue("Luhn should be valid for the non-Visa card", rule.algorithm!!.isValid(nonVisaLuhnCard))
        Assert.assertFalse("Regex should fail for non-Visa card", rule.regex!!.isValid(nonVisaLuhnCard))
    }
    //endregion

    //region Override Flag Test
    @Test
    fun `build with override flag should store it correctly`() {
        // Arrange
        val ruleWithOverride = PaymentCardNumberRule.ValidationBuilder()
            .setAllowToOverrideDefaultValidation(true)
            .build()
        val ruleWithoutOverride = PaymentCardNumberRule.ValidationBuilder()
            .setAllowToOverrideDefaultValidation(false)
            .build()

        // Assert
        Assert.assertTrue(
            "overrideDefaultValidation should be true",
            ruleWithOverride.overrideDefaultValidation
        )
        Assert.assertFalse(
            "overrideDefaultValidation should be false",
            ruleWithoutOverride.overrideDefaultValidation
        )
    }
}