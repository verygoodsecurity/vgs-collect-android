package com.verygoodsecurity.vgscollect.view.card.validation.rules

import com.verygoodsecurity.vgscollect.view.card.validation.LengthValidator
import com.verygoodsecurity.vgscollect.view.card.validation.RegexValidator

class VGSInfoRule private constructor(
    regexValidator: RegexValidator?,
    lengthValidator: LengthValidator?
) : ValidationRule(regexValidator, lengthValidator) {

    /**
     * This class provides an API for set up rules for validation person name.
     */
    class ValidationBuilder {

        /** The Regex for validation input. */
        private var regex: RegexValidator? = null

        /** The minimum length of the person name which will support. */
        private var minLength = -1

        /** The maximum length of the person name which will support. */
        private var maxLength = -1

        /** Length validation result listener. */
        private var lengthValidationResultListener: VGSValidationResultListener? = null

        /** Configure Regex for validation input. */
        fun setRegex(
            regex: String,
            resultListener: VGSValidationResultListener? = null
        ): ValidationBuilder {
            this.regex = RegexValidator(regex, resultListener)
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

        private fun getLengthValidator(): LengthValidator? {
            return if (minLength != -1 && maxLength != -1) {
                LengthValidator(
                    (minLength..maxLength).toList().toTypedArray(),
                    lengthValidationResultListener
                )
            } else {
                null
            }
        }

        /** Creates a rule. */
        fun build(): VGSInfoRule {
            return VGSInfoRule(
                regex,
                getLengthValidator()
            )
        }
    }

}