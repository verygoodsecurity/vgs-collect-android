package com.verygoodsecurity.vgscollect.view.card.validation.rules

import com.verygoodsecurity.vgscollect.view.card.validation.CheckSumValidator
import com.verygoodsecurity.vgscollect.view.card.validation.LengthValidator
import com.verygoodsecurity.vgscollect.view.card.validation.RegexValidator
import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm
import kotlin.math.max
import kotlin.math.min

/**
 * [com.verygoodsecurity.vgscollect.widget.VGSEditText] validation rule.
 */
class VGSInfoRule private constructor(
    checkSumValidator: CheckSumValidator?,
    regex: RegexValidator?,
    length: LengthValidator?,
) : ValidationRule(checkSumValidator, regex, length, null) {

    /**
     * This class provides an API for set up rules for validation person name.
     */
    class ValidationBuilder {

        /** The regex for validation input. */
        private var checkSumValidator: CheckSumValidator? = null

        /** The regex for validation input. */
        private var regex: RegexValidator? = null

        /** The length range for validation input. */
        private var length: LengthValidator? = null

        /** Configure behavior for validation checkSum. */
        @JvmOverloads
        fun setAlgorithm(
            algorithm: ChecksumAlgorithm,
            errorMsg: String = CheckSumValidator.DEFAULT_ERROR_MSG
        ) = this.apply { this.checkSumValidator = CheckSumValidator(algorithm, errorMsg) }

        /** Configure regex for validation input. */
        @JvmOverloads
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
            errorMsg: String
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

        /** Creates a rule. */
        fun build() = VGSInfoRule(checkSumValidator, regex, length)
    }
}