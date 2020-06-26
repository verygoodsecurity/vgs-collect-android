package com.verygoodsecurity.vgscollect.view.card.validation.bank

/**
 * This rule provides a simplified mechanism to improve default behavior of the bank card number field.
 */
data class BankCardNumberRule private constructor(
    internal val algorithm: ChecksumAlgorithm?,
    internal val length:Array<Int>?
) {

    /**
     * This class provides an API for set up rules for validation unknown bank card brands.
     *
     * It is recommended if you may have a lot of local or special brands in you system.
     */
    class ValidationRuleBuilder {

        /** The algorithm for validation checkSum. */
        private var algorithm: ChecksumAlgorithm? = null

        /** The array of the card's number which will support. */
        private var length:Array<Int>? = null

        /** The minimum length of the card's number which will support. */
        private var minLength = -1

        /** The maximum length of the card's number which will support. */
        private var maxLength = -1

        /** Configure behavior for validation checkSum. */
        fun setAlgorithm(algorithm: ChecksumAlgorithm): ValidationRuleBuilder {
            this.algorithm = algorithm
            return this
        }

        /** Configure the array of the card's number which will support. */
        fun setAllowableNumberLength(length:Array<Int>): ValidationRuleBuilder {
            this.length = length
            return this
        }

        /** Configure minimum length of the card's number which will support. */
        fun setAllowableMinLength(length:Int): ValidationRuleBuilder {
            if(maxLength == -1) {
                maxLength = 19
            }
            minLength = if(length > maxLength) {
                maxLength
            } else {
                length
            }
            return this
        }

        /** Configure maximum length of the card's number which will support. */
        fun setAllowableMaxLength(length:Int): ValidationRuleBuilder {
            if(minLength == -1) {
                minLength = 13
            }
            if(length < minLength) {
                minLength = length
            }
            maxLength = length
            return this
        }

        /** Creates a rule. */
        fun build(): BankCardNumberRule {
            val range = when {
                !length.isNullOrEmpty() -> length
                minLength != -1 && maxLength != -1 -> (minLength..maxLength).toList().toTypedArray()
                else -> null
            }

            return BankCardNumberRule(
                algorithm,
                range
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BankCardNumberRule

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