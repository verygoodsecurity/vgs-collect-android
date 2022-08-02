package com.verygoodsecurity.vgscollect.view.card.validation.rules

class VGSInfoRule private constructor(
    regex: String?,
    regexResultListener: VGSValidationResultListener?,
    length: Array<Int>?,
    lengthResultListener: VGSValidationResultListener?
) : ValidationRule(regex, regexResultListener, length, lengthResultListener) {

    /**
     * This class provides an API for set up rules for validation person name.
     */
    class ValidationBuilder {

        /** The Regex for validation input. */
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
        fun setLengthValidationResultListener(listener: VGSValidationResultListener): ValidationBuilder {
            this.lengthValidationResultListener = listener
            return this
        }

        private fun getRange(): Array<Int>? {
            return if (minLength != -1 && maxLength != -1) {
                (minLength..maxLength).toList().toTypedArray()
            } else {
                null
            }
        }

        /** Creates a rule. */
        fun build(): VGSInfoRule {
            return VGSInfoRule(
                regex,
                regexResultListener,
                getRange(),
                lengthValidationResultListener
            )
        }
    }

}