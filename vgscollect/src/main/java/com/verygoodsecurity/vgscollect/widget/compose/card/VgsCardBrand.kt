@file:Suppress("unused")

package com.verygoodsecurity.vgscollect.widget.compose.card

import androidx.annotation.DrawableRes
import com.verygoodsecurity.vgscollect.util.extension.except
import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsLuhnAlgorithmValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsTextLengthValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidationResult
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
    @DrawableRes val iconResourceId: Int,
) {

    companion object {

        fun detect(card: String): VgsCardBrand {
            if (card.isBlank()) return map(CardType.UNKNOWN)
            CardType.entries.toTypedArray().except(CardType.UNKNOWN).forEach {
                val matcher = Pattern.compile(it.regex).matcher(card)
                while (matcher.find()) {
                    return map(it)
                }
            }
            return map(CardType.UNKNOWN)
        }

        private fun map(cardType: CardType): VgsCardBrand {
            return VgsCardBrand(
                name = cardType.name,
                mask = cardType.mask,
                regex = cardType.regex,
                algorithm = cardType.algorithm.toChecksumAlgorithm(),
                length = cardType.rangeNumber,
                securityCodeLength = cardType.rangeCVV,
                iconResourceId = cardType.resId
            )
        }
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

internal fun VgsCardBrand.getValidators(): List<VgsTextFieldValidator> {
    val result = mutableListOf<VgsTextFieldValidator>()
    result.add( VgsTextLengthValidator(lengths = this.length))
    if (this.algorithm == VgsCardBrand.ChecksumAlgorithm.LUHN) {
        result.add(VgsLuhnAlgorithmValidator())
    }
    return result
}