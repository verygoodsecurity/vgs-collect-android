package com.verygoodsecurity.vgscollect.view.card.validation.rules

open class ValidationRule protected constructor(
    internal val regex: String?,
    internal val regexResultLister: VGSValidationResultListener?,
    internal val length: Array<Int>?,
    internal val lengthResultLister: VGSValidationResultListener?,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ValidationRule

        if (regex != other.regex) return false
        if (regexResultLister != other.regexResultLister) return false
        if (length != null) {
            if (other.length == null) return false
            if (!length.contentEquals(other.length)) return false
        } else if (other.length != null) return false
        if (lengthResultLister != other.lengthResultLister) return false

        return true
    }

    override fun hashCode(): Int {
        var result = regex?.hashCode() ?: 0
        result = 31 * result + (regexResultLister?.hashCode() ?: 0)
        result = 31 * result + (length?.contentHashCode() ?: 0)
        result = 31 * result + (lengthResultLister?.hashCode() ?: 0)
        return result
    }
}