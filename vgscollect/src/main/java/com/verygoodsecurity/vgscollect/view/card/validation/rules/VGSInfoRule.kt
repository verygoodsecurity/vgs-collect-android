package com.verygoodsecurity.vgscollect.view.card.validation.rules

import com.verygoodsecurity.vgscollect.view.card.validation.CheckSumValidator
import com.verygoodsecurity.vgscollect.view.card.validation.LengthValidator
import com.verygoodsecurity.vgscollect.view.card.validation.RegexValidator
import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm
import kotlin.math.max
import kotlin.math.min

/**
 * A validation rule for a generic text field.
 */
class VGSInfoRule private constructor(
    checkSumValidator: CheckSumValidator?,
    regex: RegexValidator?,
    length: LengthValidator?,
) : ValidationRule(checkSumValidator, regex, length, null) {

    /**
     * A builder for creating [VGSInfoRule]s.
     */
    class ValidationBuilder {

        /** The regex for validation input. */
        private var checkSumValidator: CheckSumValidator? = null

        /** The regex for validation input. */
        private var regex: RegexValidator? = null

        /** The length range for validation input. */
        private var length: LengthValidator? = null

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
        ) = this.apply { this.checkSumValidator = CheckSumValidator(algorithm, errorMsg) }

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
         * Builds the validation rule.
         */
        fun build() = VGSInfoRule(checkSumValidator, regex, length)
    }
}