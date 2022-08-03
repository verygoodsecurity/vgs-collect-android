package com.verygoodsecurity.vgscollect.view.card.validation.rules

import com.verygoodsecurity.vgscollect.view.card.validation.LengthValidator
import com.verygoodsecurity.vgscollect.view.card.validation.RegexValidator
import kotlin.math.max
import kotlin.math.min

/**
 * [com.verygoodsecurity.vgscollect.widget.PersonNameEditText] validation rule.
 */
class PersonNameRule private constructor(
    regex: RegexValidator?,
    length: LengthValidator?,
) : ValidationRule(null, regex, length, null) {

    /**
     * This class provides an API for set up rules for validation person name.
     */
    class ValidationBuilder {

        /** The regex for validation input. */
        private var regex: RegexValidator? = null

        /** The length range for validation input. */
        private var length: LengthValidator? = null

        /** Configure regex for validation input. */
        fun setRegex(
            regex: String,
            errorMsg: String = RegexValidator.DEFAULT_ERROR_MSG
        ) = this.apply { this.regex = RegexValidator(regex, errorMsg) }

        /** Configure minimum length which will support. */
        fun setAllowableMinLength(length: Int, ) = this.apply {
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

        /** Creates a rule. */
        fun build() = PersonNameRule(regex, length)
    }
}