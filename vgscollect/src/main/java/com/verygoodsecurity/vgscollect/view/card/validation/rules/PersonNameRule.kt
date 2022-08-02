package com.verygoodsecurity.vgscollect.view.card.validation.rules

/**
 * This rule provides a simplified mechanism to improve default behavior of field which include person name.
 */
class PersonNameRule private constructor(
    regex: String?,
    regexResultLister: VGSValidationResultListener?,
    length: Array<Int>?,
    lengthResultLister: VGSValidationResultListener?,
) : ValidationRule(regex, regexResultLister, length, lengthResultLister) {

    /**
     * This class provides an API for set up rules for validation person name.
     */
    class ValidationBuilder {

        /** Regex for validation input. */
        private var regex: String? = null

        /** Regex validation result listener. */
        private var regexResultListener: VGSValidationResultListener? = null

        /** The minimum length of the person name which will support. */
        private var minLength = -1

        /** The maximum length of the person name which will support. */
        private var maxLength = -1

        /** Length validation result listener. */
        private var lengthValidationResultListener: VGSValidationResultListener? = null

        /** Configure Regex for validation input. */
        fun setRegex(
            regex: String,
            listener: VGSValidationResultListener? = null
        ): ValidationBuilder {
            this.regex = regex
            this.regexResultListener = listener
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
        fun setLengthValidationResultListener(listener: VGSValidationResultListener?): ValidationBuilder {
            this.lengthValidationResultListener = listener
            return this
        }

        /** Creates a rule. */
        fun build(): PersonNameRule {
            val range = if (minLength != -1 && maxLength != -1) {
                (minLength..maxLength).toList().toTypedArray()
            } else {
                null
            }

            return PersonNameRule(
                regex,
                regexResultListener,
                range,
                lengthValidationResultListener
            )
        }
    }

}