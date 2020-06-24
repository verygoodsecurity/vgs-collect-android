package com.verygoodsecurity.vgscollect.view.card

data class BrandParams(
    val mask:String = "#### #### #### #### ###",
    val algorithm:ChecksumAlgorithm = ChecksumAlgorithm.NONE,
    val rangeNumber:Array<Int> = CardType.UNDEFINED.rangeNumber,
    val rangeCVV:Array<Int> = CardType.UNDEFINED.rangeCVV
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BrandParams

        if (mask != other.mask) return false
        if (algorithm != other.algorithm) return false
        if (!rangeNumber.contentEquals(other.rangeNumber)) return false
        if (!rangeCVV.contentEquals(other.rangeCVV)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mask.hashCode()
        result = 31 * result + algorithm.hashCode()
        result = 31 * result + rangeNumber.contentHashCode()
        result = 31 * result + rangeCVV.contentHashCode()
        return result
    }
}