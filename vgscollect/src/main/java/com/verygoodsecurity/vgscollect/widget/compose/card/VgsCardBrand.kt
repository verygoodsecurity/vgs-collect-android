@file:Suppress("unused")

package com.verygoodsecurity.vgscollect.widget.compose.card

import androidx.annotation.DrawableRes
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.util.extension.except
import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsLuhnAlgorithmValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsTextLengthValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidator
import java.util.regex.Pattern
import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm as LegacyChecksumAlgorithm

class VgsCardBrand private constructor(
    val name: String,
    val mask: String,
    val regex: String,
    val algorithm: ChecksumAlgorithm,
    val length: Array<Int>,
    val securityCodeLength: Array<Int>,
    @DrawableRes val cardIcon: Int,
    @DrawableRes val securityCodeIcon: Int,
) {

    companion object {

        val UNKNOWN = map(CardType.UNKNOWN)

        fun detect(card: String): VgsCardBrand {
            if (card.isBlank()) return UNKNOWN
            CardType.entries.toTypedArray().except(CardType.UNKNOWN).forEach {
                val matcher = Pattern.compile(it.regex).matcher(card)
                while (matcher.find()) {
                    return map(it)
                }
            }
            return UNKNOWN
        }

        private fun map(cardType: CardType): VgsCardBrand {
            return VgsCardBrand(
                name = cardType.name,
                mask = cardType.mask,
                regex = cardType.regex,
                algorithm = cardType.algorithm.toChecksumAlgorithm(),
                length = cardType.rangeNumber,
                securityCodeLength = cardType.rangeCVV,
                cardIcon = cardType.resId,
                securityCodeIcon = getIcon(cardType)
            )
        }

        @DrawableRes
        private fun getIcon(cardType: CardType): Int = when (cardType) {
            CardType.AMERICAN_EXPRESS -> R.drawable.ic_card_back_preview_dark_4
            else -> R.drawable.ic_card_back_preview_dark
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VgsCardBrand) return false

        return name == other.name &&
                mask == other.mask &&
                regex == other.regex &&
                algorithm == other.algorithm &&
                length.contentEquals(other.length) &&
                securityCodeLength.contentEquals(other.securityCodeLength) &&
                cardIcon == other.cardIcon &&
                securityCodeIcon == other.securityCodeIcon
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + mask.hashCode()
        result = 31 * result + regex.hashCode()
        result = 31 * result + algorithm.hashCode()
        result = 31 * result + length.contentHashCode()
        result = 31 * result + securityCodeLength.contentHashCode()
        result = 31 * result + cardIcon
        result = 31 * result + securityCodeIcon
        return result
    }

    enum class ChecksumAlgorithm {

        LUHN,
        NONE
    }
}

internal fun LegacyChecksumAlgorithm.toChecksumAlgorithm(): VgsCardBrand.ChecksumAlgorithm {
    return when (this) {
        LegacyChecksumAlgorithm.LUHN, LegacyChecksumAlgorithm.ANY -> VgsCardBrand.ChecksumAlgorithm.LUHN
        LegacyChecksumAlgorithm.NONE -> VgsCardBrand.ChecksumAlgorithm.NONE
    }
}

internal fun VgsCardBrand.getCardNumberValidators(): List<VgsTextFieldValidator> {
    val result = mutableListOf<VgsTextFieldValidator>()
    result.add(VgsTextLengthValidator(lengths = this.length))
    if (this.algorithm == VgsCardBrand.ChecksumAlgorithm.LUHN) {
        result.add(VgsLuhnAlgorithmValidator())
    }
    return result
}

internal fun VgsCardBrand.getSecurityCodeValidators(): List<VgsTextFieldValidator> {
    return listOf(VgsTextLengthValidator(lengths = this.securityCodeLength))
}