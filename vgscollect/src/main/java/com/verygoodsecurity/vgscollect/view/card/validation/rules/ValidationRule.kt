package com.verygoodsecurity.vgscollect.view.card.validation.rules

open class ValidationRule protected constructor(
    internal val regex: String?,
    internal val length:Array<Int>?
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ValidationRule

        if (regex != other.regex) return false
        if (length != null) {
            if (other.length == null) return false
            if (!length.contentEquals(other.length)) return false
        } else if (other.length != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = regex?.hashCode() ?: 0
        result = 31 * result + (length?.contentHashCode() ?: 0)
        return result
    }

}