package com.verygoodsecurity.vgscollect.view.card.validation.rules

import com.verygoodsecurity.vgscollect.view.card.validation.CheckSumValidator
import com.verygoodsecurity.vgscollect.view.card.validation.LengthMatchValidator
import com.verygoodsecurity.vgscollect.view.card.validation.LengthValidator
import com.verygoodsecurity.vgscollect.view.card.validation.RegexValidator
import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm
import kotlin.math.max
import kotlin.math.min

/**
 * [com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText] validation rule.
 */
class PaymentCardNumberRule private constructor(
    algorithm: CheckSumValidator?,
    regex: RegexValidator?,
    length: LengthValidator?,
    lengthMatch: LengthMatchValidator?,
    internal val overrideDefaultValidation: Boolean
) : ValidationRule(algorithm, regex, length, lengthMatch) {

    /**
     * This class provides an API for set up rules for validation unknown bank card brands.
     *
     * It is recommended if you may have a lot of local or special brands in you system.
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

        /** Configure behavior for validation checkSum. */
        fun setAlgorithm(
            algorithm: ChecksumAlgorithm,
            errorMsg: String = CheckSumValidator.DEFAULT_ERROR_MSG
        ) = this.apply { this.algorithm = CheckSumValidator(algorithm, errorMsg) }

        /** Configure regex for validation input. */
        fun setRegex(
            regex: String,
            errorMsg: String = RegexValidator.DEFAULT_ERROR_MSG
        ) = this.apply { this.regex = RegexValidator(regex, errorMsg) }

        /** Configure minimum length which will support. */
        fun setAllowableMinLength(length: Int) = this.apply {
            this.length = this.length?.let {
                it.copy(min = min(it.max, length))
            } ?: LengthValidator(length, MAX_LENGTH)
        }

        /** Configure minimum length which will support. */
        fun setAllowableMinLength(
            length: Int,
            errorMsg: String = LengthValidator.DEFAULT_ERROR_MSG
        ) = this.apply {
            this.length = this.length?.let {
                it.copy(min = min(it.max, length), errorMsg = errorMsg)
            } ?: LengthValidator(length, MAX_LENGTH, errorMsg)
        }

        /** Configure maximum length which will support. */
        fun setAllowableMaxLength(length: Int) = this.apply {
            this.length = this.length?.let {
                it.copy(max = max(it.min, length))
            } ?: LengthValidator(MIN_LENGTH, length)
        }

        /** Configure maximum length which will support. */
        fun setAllowableMaxLength(
            length: Int,
            errorMsg: String
        ) = this.apply {
            this.length = this.length?.let {
                it.copy(max = max(it.min, length), errorMsg = errorMsg)
            } ?: LengthValidator(MIN_LENGTH, length, errorMsg)
        }

        /** Configure the array of lengths which will support. */
        fun setAllowableNumberLength(
            length: Array<Int>,
            errorMsg: String = LengthMatchValidator.DEFAULT_ERROR_MSG
        ) = this.apply {
            this.lengthMatch = LengthMatchValidator(length, errorMsg)
        }

        /** Determines whether the Collect SDK can override default validation rules. */
        fun setAllowToOverrideDefaultValidation(canOverride: Boolean) = this.apply {
            this.overrideDefaultValidation = canOverride
        }

        /** Creates a rule. */
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