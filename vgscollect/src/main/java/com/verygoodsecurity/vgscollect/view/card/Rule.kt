package com.verygoodsecurity.vgscollect.view.card

data class Rule private constructor(
    val algorithm: ChecksumAlgorithm,
    val length:Array<Int>
) {

    class RuleBuilder {
        private var algorithm:ChecksumAlgorithm = ChecksumAlgorithm.ANY
        private var length:Array<Int>? = null
        private var minLength = 13
        private var maxLength = 19

        fun setAlgorithm(algorithm:ChecksumAlgorithm):RuleBuilder {
            this.algorithm = algorithm
            return this
        }

        fun setLength(length:Array<Int>):RuleBuilder {
            this.length = length
            return this
        }

        fun setMinLength(length:Int):RuleBuilder {
            minLength = if(minLength > maxLength) {
                maxLength
            } else {
                length
            }
            return this
        }

        fun setMaxLength(length:Int):RuleBuilder {
            maxLength = if(maxLength < minLength) {
                maxLength
            } else {
                length
            }
            return this
        }

        fun build():Rule {
            val range = if(length.isNullOrEmpty()) {
                (minLength..maxLength).toList().toTypedArray()
            } else {
                length!!
            }
            return Rule(algorithm, range)
        }

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rule

        if (algorithm != other.algorithm) return false
        if (!length.contentEquals(other.length)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = algorithm.hashCode()
        result = 31 * result + length.contentHashCode()
        return result
    }
}