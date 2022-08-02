package com.verygoodsecurity.vgscollect.view.card.validation.rules

import com.verygoodsecurity.vgscollect.view.card.validation.CheckSumValidator
import com.verygoodsecurity.vgscollect.view.card.validation.LengthValidator
import com.verygoodsecurity.vgscollect.view.card.validation.RegexValidator
import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm

/**
 * This rule provides a simplified mechanism to improve default behavior of the bank card number field.
 */
class PaymentCardNumberRule private constructor(
    internal val algorithmValidator: CheckSumValidator?,
    lengthValidator: LengthValidator?,
    regexValidator: RegexValidator?,
    internal val canOverrideDefaultValidation: Boolean
) : ValidationRule(regexValidator, lengthValidator) {

    /**
     * This class provides an API for set up rules for validation unknown bank card brands.
     *
     * It is recommended if you may have a lot of local or special brands in you system.
     */
    class ValidationBuilder {

        /** The algorithm for validation checkSum. */
        private var algorithmValidator: CheckSumValidator? = null

        /** The array of the card's number which will support. */
        private var length: Array<Int>? = null

        /** The minimum length of the card's number which will support. */
        private var minLength = -1

        /** The maximum length of the card's number which will support. */
        private var maxLength = -1

        /** Length validation result listener. */
        private var onLengthValidationResult: ((isSuccessful: Boolean) -> Unit)? = null

        /** Determines whether the Collect SDK can replace default validation rules by configured with ValidationBuilder. */
        private var canOverrideDefaultValidation = false

        /** The Regex for validation input. */
        private var regexValidator: RegexValidator? = null

        /** Configure behavior for validation checkSum. */
        fun setAlgorithm(
            algorithm: ChecksumAlgorithm,
            onResult: ((isSuccessful: Boolean) -> Unit)? = null
        ): ValidationBuilder {
            this.algorithmValidator = CheckSumValidator(algorithm, onResult)
            return this
        }

        /** Configure the array of the card's number which will support. */
        fun setAllowableNumberLength(length: Array<Int>): ValidationBuilder {
            this.length = length
            return this
        }

        /** Configure minimum length of the card's number which will support. */
        fun setAllowableMinLength(length: Int): ValidationBuilder {
            if (maxLength == -1) {
                maxLength = 19
            }
            minLength = if (length > maxLength) {
                maxLength
            } else {
                length
            }
            return this
        }

        /** Configure maximum length of the card's number which will support. */
        fun setAllowableMaxLength(length: Int): ValidationBuilder {
            if (minLength == -1) {
                minLength = 13
            }
            if (length < minLength) {
                minLength = length
            }
            maxLength = length
            return this
        }

        /** Set length validation listener. */
        fun setLengthValidationResultListener(listener: (isSuccessful: Boolean) -> Unit): ValidationBuilder {
            this.onLengthValidationResult = listener
            return this
        }

        /** Determines whether the Collect SDK can override default validation rules. */
        fun setAllowToOverrideDefaultValidation(canOverride: Boolean): ValidationBuilder {
            canOverrideDefaultValidation = canOverride
            return this
        }


        /** Configure Regex for validation input. */
        fun setRegex(
            regex: String,
            onResult: ((isSuccessful: Boolean) -> Unit)? = null
        ): ValidationBuilder {
            this.regexValidator = RegexValidator(regex, onResult)
            return this
        }

        /** Creates a rule. */
        fun build(): PaymentCardNumberRule {
            val lengthValidator = when {
                !length.isNullOrEmpty() -> LengthValidator(length!!, onLengthValidationResult)
                minLength != -1 && maxLength != -1 -> LengthValidator(
                    (minLength..maxLength).toList().toTypedArray(), onLengthValidationResult
                )
                else -> null
            }

            return PaymentCardNumberRule(
                algorithmValidator,
                lengthValidator,
                regexValidator,
                canOverrideDefaultValidation
            )
        }
    }

}