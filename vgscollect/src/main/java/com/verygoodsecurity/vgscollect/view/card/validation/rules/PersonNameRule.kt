package com.verygoodsecurity.vgscollect.view.card.validation.rules

import com.verygoodsecurity.vgscollect.view.card.validation.LengthValidator
import com.verygoodsecurity.vgscollect.view.card.validation.RegexValidator

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
            listener: VGSValidationResultListener? = null
        ) = this.apply { this.regex = RegexValidator(regex, listener) }

        /** Configure minimum length which will support. */
        fun setAllowableMinLength(length: Int) = this.apply {
            this.length = this.length?.copy(min = length) ?: LengthValidator(length, MAX_LENGTH)
        }

        /** Configure maximum length which will support. */
        fun setAllowableMaxLength(length: Int) = this.also {
            this.length = this.length?.copy(max = length) ?: LengthValidator(MIN_LENGTH, length)
        }

        /** Creates a rule. */
        fun build() = PersonNameRule(regex, length)
    }
}