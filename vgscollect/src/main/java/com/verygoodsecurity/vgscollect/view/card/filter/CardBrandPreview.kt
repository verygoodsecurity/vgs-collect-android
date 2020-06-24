package com.verygoodsecurity.vgscollect.view.card.filter

import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.validation.bank.ChecksumAlgorithm

/** @suppress */
data class CardBrandPreview(
    val cardType: CardType = CardType.UNDEFINED,
    val regex:String = CardType.UNDEFINED.regex,
    val name:String? = CardType.UNDEFINED.name,
    val resId:Int = CardType.UNDEFINED.resId,
    var currentMask:String = CardType.UNDEFINED.mask,
    var algorithm: ChecksumAlgorithm = ChecksumAlgorithm.NONE,
    var numberLength: Array<Int> = CardType.UNDEFINED.rangeNumber,
    var cvcLength: Array<Int> = CardType.UNDEFINED.rangeCVV,
    val successfullyDetected:Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CardBrandPreview

        if (cardType != other.cardType) return false
        if (regex != other.regex) return false
        if (name != other.name) return false
        if (resId != other.resId) return false
        if (currentMask != other.currentMask) return false
        if (algorithm != other.algorithm) return false
        if (!numberLength.contentEquals(other.numberLength)) return false
        if (!cvcLength.contentEquals(other.cvcLength)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cardType.hashCode()
        result = 31 * result + regex.hashCode()
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + resId
        result = 31 * result + currentMask.hashCode()
        result = 31 * result + algorithm.hashCode()
        result = 31 * result + numberLength.contentHashCode()
        result = 31 * result + cvcLength.contentHashCode()
        return result
    }

}