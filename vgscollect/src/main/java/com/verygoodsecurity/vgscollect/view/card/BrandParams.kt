package com.verygoodsecurity.vgscollect.view.card

import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm

/**
 * Creates a new set of card brand parameters.
 */
data class BrandParams(

    /** Represents format of the current card's number. */
    val mask:String = "#### #### #### #### ###",

    /** The algorithm for validation checkSum. */
    val algorithm: ChecksumAlgorithm = ChecksumAlgorithm.NONE,

    /** The length of the card's number which a brand supported. */
    val rangeNumber:Array<Int> = CardType.UNKNOWN.rangeNumber,

    /** The length of the card's CVC number which a brand supported. */
    val rangeCVV:Array<Int> = CardType.UNKNOWN.rangeCVV
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