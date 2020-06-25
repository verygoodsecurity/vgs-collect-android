package com.verygoodsecurity.vgscollect.view.card.validation.bank

data class Rule private constructor(
    val algorithm: ChecksumAlgorithm?,
    val length:Array<Int>?
) {

    class ValidationRuleBuilder {
        private var algorithm: ChecksumAlgorithm? = null
        private var length:Array<Int>? = null
        private var minLength = -1
        private var maxLength = -1

        fun setAlgorithm(algorithm: ChecksumAlgorithm): ValidationRuleBuilder {
            this.algorithm = algorithm
            return this
        }

        fun setLength(length:Array<Int>): ValidationRuleBuilder {
            this.length = length
            return this
        }

        fun setMinLength(length:Int): ValidationRuleBuilder {
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

        fun setMaxLength(length:Int): ValidationRuleBuilder {
            if(minLength == -1) {
                minLength = 13
            }
            if(length < minLength) {
                minLength = length
            }
            maxLength = length
            return this
        }

        fun build(): Rule {
            val range = when {
                length.isNullOrEmpty() &&
                        minLength != -1 &&
                        maxLength != -1 -> (minLength..maxLength).toList().toTypedArray()
                !length.isNullOrEmpty() -> length
                else -> null
            }

            return Rule(
                algorithm,
                range
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rule

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