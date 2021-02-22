package com.verygoodsecurity.vgscollect.view.card.validation.rules

import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm

/**
 * This rule provides a simplified mechanism to improve default behavior of the bank card number field.
 */
data class PaymentCardNumberRule private constructor(
    internal val algorithm: ChecksumAlgorithm?,
    internal val length: Array<Int>?,
    internal val regex: String?,
    internal val canOverrideDefaultValidation: Boolean
) {

    /**
     * This class provides an API for set up rules for validation unknown bank card brands.
     *
     * It is recommended if you may have a lot of local or special brands in you system.
     */
    class ValidationBuilder {

        /** The algorithm for validation checkSum. */
        private var algorithm: ChecksumAlgorithm? = null

        /** The array of the card's number which will support. */
        private var length: Array<Int>? = null

        /** The minimum length of the card's number which will support. */
        private var minLength = -1

        /** The maximum length of the card's number which will support. */
        private var maxLength = -1

        /** Determines whether the Collect SDK can replace default validation rules by configured with ValidationBuilder. */
        private var canOverrideDefaultValidation = false

        /** The Regex for validation input. */
        private var regex: String? = null

        /** Configure behavior for validation checkSum. */
        fun setAlgorithm(algorithm: ChecksumAlgorithm): ValidationBuilder {
            this.algorithm = algorithm
            return this
        }

        /** Configure the array of the card's number which will support. */
        fun setAllowableNumberLength(length: Array<Int>): ValidationBuilder {
            this.length = length
            return this
        }

        /** Configure minimum length of the card's number which will support. */
        fun setAllowableMinLength(length: Int): ValidationBuilder {
            if (maxLength == -1) {
                maxLength = 19
            }
            minLength = if (length > maxLength) {
                maxLength
            } else {
                length
            }
            return this
        }

        /** Configure maximum length of the card's number which will support. */
        fun setAllowableMaxLength(length: Int): ValidationBuilder {
            if (minLength == -1) {
                minLength = 13
            }
            if (length < minLength) {
                minLength = length
            }
            maxLength = length
            return this
        }

        /** Determines whether the Collect SDK can override default validation rules. */
        fun setAllowToOverrideDefaultValidation(canOverride: Boolean): ValidationBuilder {
            canOverrideDefaultValidation = canOverride
            return this
        }


        /** Configure Regex for validation input. */
        fun setRegex(regex: String): ValidationBuilder {
            this.regex = regex
            return this
        }

        /** Creates a rule. */
        fun build(): PaymentCardNumberRule {
            val range = when {
                !length.isNullOrEmpty() -> length
                minLength != -1 && maxLength != -1 -> (minLength..maxLength).toList().toTypedArray()
                else -> null
            }

            return PaymentCardNumberRule(
                algorithm,
                range,
                regex,
                canOverrideDefaultValidation
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PaymentCardNumberRule

        if (algorithm != other.algorithm) return false
        if (length != null) {
            if (other.length == null) return false
            if (!length.contentEquals(other.length)) return false
        } else if (other.length != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = algorithm?.hashCode() ?: 0
        result = 31 * result + (length?.contentHashCode() ?: 0)
        return result
    }
}