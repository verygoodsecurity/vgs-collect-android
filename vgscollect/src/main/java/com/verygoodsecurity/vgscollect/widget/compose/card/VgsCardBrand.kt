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

/**
 * Describes a payment card brand and the rules used to recognise and validate it.
 *
 * The card-number field auto-detects the brand from the current input against
 * the brands it was created with (see `supportedCardBrands` on
 * [com.verygoodsecurity.vgscollect.widget.compose.state.VgsCardNumberTextFieldState]).
 * The detected brand is exposed as `cardBrand` on the state so the UI can
 * react — e.g. by drawing [cardIcon] in a `trailingIcon`.
 *
 * Use [DEFAULT] for the built-in brands or build your own list (custom brand
 * + [DEFAULT]) to extend detection.
 *
 * @param name display name of the brand (e.g. `"VISA"`).
 * @param mask formatted display pattern, e.g. `"#### #### #### ####"`.
 * @param regex pattern that matches card numbers belonging to this brand.
 * @param algorithm checksum used to validate the full number.
 * @param length set of valid total card-number lengths.
 * @param securityCodeLength set of valid CVC lengths for this brand.
 * @param cardIcon drawable resource for the brand's front-of-card icon.
 * @param securityCodeIcon drawable resource for the matching CVC icon.
 */
data class VgsCardBrand(
    val name: String,
    val mask: String,
    val regex: String,
    val algorithm: ChecksumAlgorithm,
    val length: Array<Int>,
    val securityCodeLength: Array<Int>,
    @param:DrawableRes val cardIcon: Int,
    @param:DrawableRes val securityCodeIcon: Int,
) {

    companion object {

        /** Brand returned when no other brand matches the current input. */
        val UNKNOWN = map(CardType.UNKNOWN)

        /** Built-in brands recognised out of the box. */
        val DEFAULT = CardType.entries.toTypedArray().except(CardType.UNKNOWN).map { map(it) }

        /**
         * Returns the first brand from [brands] whose [regex] matches [card],
         * or [UNKNOWN] when nothing matches.
         */
        fun detect(card: String, brands: List<VgsCardBrand> = DEFAULT): VgsCardBrand {
            if (card.isBlank()) return UNKNOWN
            brands.forEach { brand ->
                val matcher = Pattern.compile(brand.regex).matcher(card)
                if (matcher.find()) {
                    return brand
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

    /** Checksum algorithm used to validate card numbers for a brand. */
    enum class ChecksumAlgorithm {

        /** Luhn (mod-10) checksum, used by most major card brands. */
        LUHN,

        /** No checksum validation. */
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