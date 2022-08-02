package com.verygoodsecurity.vgscollect.view.card.validation.rules

import com.verygoodsecurity.vgscollect.view.card.validation.LengthValidator
import com.verygoodsecurity.vgscollect.view.card.validation.RegexValidator

/**
 * This rule provides a simplified mechanism to improve default behavior of field which include person name.
 */
class PersonNameRule private constructor(
    regexValidator: RegexValidator?,
    lengthValidator: LengthValidator?
) : ValidationRule(regexValidator, lengthValidator) {

    /**
     * This class provides an API for set up rules for validation person name.
     */
    class ValidationBuilder {

        /** The Regex for validation input. */
        private var regexValidator: RegexValidator? = null

        /** The minimum length of the person name which will support. */
        private var minLength = -1

        /** The maximum length of the person name which will support. */
        private var maxLength = -1

        /** Length validation result listener. */
        private var lengthValidationResultListener: VGSValidationResultListener? = null

        /** Configure Regex for validation input. */
        fun setRegex(
            regex: String,
            onResult: ((isSuccessful: Boolean) -> Unit)? = null
        ): ValidationBuilder {
            this.regexValidator = RegexValidator(regex, onResult)
            return this
        }

        /** Configure minimum length of the name which will support. */
        fun setAllowableMinLength(length: Int): ValidationBuilder {
            if (maxLength == -1) {
                maxLength = 256
            }
            minLength = if (length > maxLength) {
                maxLength
            } else {
                length
            }
            return this
        }

        /** Configure maximum length of the name which will support. */
        fun setAllowableMaxLength(length: Int): ValidationBuilder {
            if (minLength == -1) {
                minLength = 1
            }
            if (length < minLength) {
                minLength = length
            }
            maxLength = length
            return this
        }

        /** Set length validation listener. */
        fun setLengthValidationResultListener(listener: VGSValidationResultListener): ValidationBuilder {
            this.lengthValidationResultListener = listener
            return this
        }

        /** Creates a rule. */
        fun build(): PersonNameRule {
            val lengthValidator = if (minLength != -1 && maxLength != -1) {
                LengthValidator(
                    (minLength..maxLength).toList().toTypedArray(),
                    lengthValidationResultListener
                )
            } else {
                null
            }

            return PersonNameRule(
                regexValidator,
                lengthValidator
            )
        }
    }

}