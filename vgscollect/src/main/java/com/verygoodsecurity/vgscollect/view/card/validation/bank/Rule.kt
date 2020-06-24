package com.verygoodsecurity.vgscollect.view.card.validation.bank

data class Rule private constructor(
    val algorithm: ChecksumAlgorithm?,
    val length:Array<Int>?
) {

    class RuleBuilder {
        private var algorithm: ChecksumAlgorithm? = null
        private var length:Array<Int>? = null
        private var minLength = -1
        private var maxLength = -1

        fun setAlgorithm(algorithm: ChecksumAlgorithm): RuleBuilder {
            this.algorithm = algorithm
            return this
        }

        fun setLength(length:Array<Int>): RuleBuilder {
            this.length = length
            return this
        }

        fun setMinLength(length:Int): RuleBuilder {
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

        fun setMaxLength(length:Int): RuleBuilder {
            if(minLength == -1) {
                minLength = 13
            }
            maxLength = if(length < minLength) {
                minLength
            } else {
                length
            }
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
}