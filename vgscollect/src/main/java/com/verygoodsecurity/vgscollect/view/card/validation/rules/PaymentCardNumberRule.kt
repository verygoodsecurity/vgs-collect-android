package com.verygoodsecurity.vgscollect.view.card.validation.rules

import com.verygoodsecurity.vgscollect.view.card.validation.CheckSumValidator
import com.verygoodsecurity.vgscollect.view.card.validation.LengthMatchValidator
import com.verygoodsecurity.vgscollect.view.card.validation.LengthValidator
import com.verygoodsecurity.vgscollect.view.card.validation.RegexValidator
import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm
import kotlin.math.max
import kotlin.math.min

/**
 * A validation rule for a payment card number.
 */
class PaymentCardNumberRule private constructor(
    algorithm: CheckSumValidator?,
    regex: RegexValidator?,
    length: LengthValidator?,
    lengthMatch: LengthMatchValidator?,
    internal val overrideDefaultValidation: Boolean
) : ValidationRule(algorithm, regex, length, lengthMatch) {

    /**
     * A builder for creating [PaymentCardNumberRule]s.
     */
    class ValidationBuilder {

        /** The algorithm for validation checkSum. */
        private var algorithm: CheckSumValidator? = null

        /** The regex for validation input. */
        private var regex: RegexValidator? = null

        /** The length range for validation input. */
        private var length: LengthValidator? = null

        /** The length match for validation input. */
        private var lengthMatch: LengthMatchValidator? = null

        /** Determines whether the Collect SDK can replace default validation rules by configured with ValidationBuilder. */
        private var overrideDefaultValidation = false

        /**
         * Sets the checksum algorithm for validation.
         *
         * @param algorithm The checksum algorithm.
         * @param errorMsg The error message to display if validation fails.
         */
        @JvmOverloads
        fun setAlgorithm(
            algorithm: ChecksumAlgorithm,
            errorMsg: String = CheckSumValidator.DEFAULT_ERROR_MSG
        ) = this.apply { this.algorithm = CheckSumValidator(algorithm, errorMsg) }

        /**
         * Sets the regex for validation.
         *
         * @param regex The regex.
         * @param errorMsg The error message to display if validation fails.
         */
        @JvmOverloads
        fun setRegex(
            regex: String,
            errorMsg: String = RegexValidator.DEFAULT_ERROR_MSG
        ) = this.apply { this.regex = RegexValidator(regex, errorMsg) }

        /**
         * Sets the minimum allowable length.
         *
         * @param length The minimum length.
         */
        fun setAllowableMinLength(length: Int) = this.apply {
            this.length = this.length?.let {
                it.copy(min = min(it.max, length))
            } ?: LengthValidator(length, MAX_LENGTH)
        }

        /**
         * Sets the minimum allowable length.
         *
         * @param length The minimum length.
         * @param errorMsg The error message to display if validation fails.
         */
        fun setAllowableMinLength(
            length: Int,
            errorMsg: String
        ) = this.apply {
            this.length = this.length?.let {
                it.copy(min = min(it.max, length), errorMsg = errorMsg)
            } ?: LengthValidator(length, MAX_LENGTH, errorMsg)
        }

        /**
         * Sets the maximum allowable length.
         *
         * @param length The maximum length.
         */
        fun setAllowableMaxLength(length: Int) = this.apply {
            this.length = this.length?.let {
                it.copy(max = max(it.min, length))
            } ?: LengthValidator(MIN_LENGTH, length)
        }

        /**
         * Sets the maximum allowable length.
         *
         * @param length The maximum length.
         * @param errorMsg The error message to display if validation fails.
         */
        fun setAllowableMaxLength(
            length: Int,
            errorMsg: String
        ) = this.apply {
            this.length = this.length?.let {
                it.copy(max = max(it.min, length), errorMsg = errorMsg)
            } ?: LengthValidator(MIN_LENGTH, length, errorMsg)
        }

        /**
         * Sets the allowable lengths.
         *
         * @param length The allowable lengths.
         * @param errorMsg The error message to display if validation fails.
         */
        @JvmOverloads
        fun setAllowableNumberLength(
            length: Array<Int>,
            errorMsg: String = LengthMatchValidator.DEFAULT_ERROR_MSG
        ) = this.apply {
            this.lengthMatch = LengthMatchValidator(length, errorMsg)
        }

        /**
         * Sets whether to override the default validation rules.
         *
         * @param canOverride Whether to override the default validation rules.
         */
        fun setAllowToOverrideDefaultValidation(canOverride: Boolean) = this.apply {
            this.overrideDefaultValidation = canOverride
        }

        /**
         * Builds the validation rule.
         */
        fun build() = PaymentCardNumberRule(
            algorithm,
            regex,
            length,
            lengthMatch,
            overrideDefaultValidation
        )

        companion object {

            private const val MIN_LENGTH = 13
            private const val MAX_LENGTH = 19
        }
    }
}