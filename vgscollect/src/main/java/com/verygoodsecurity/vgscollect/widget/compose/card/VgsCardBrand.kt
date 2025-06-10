@file:Suppress("unused")

package com.verygoodsecurity.vgscollect.widget.compose.card

import androidx.annotation.DrawableRes
import com.verygoodsecurity.vgscollect.util.extension.except
import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsLuhnAlgorithmValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsTextLengthValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidationResult
import java.util.regex.Pattern
import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm as LegacyChecksumAlgorithm

class VgsCardBrand internal constructor(
    val name: String,
    val mask: String,
    val regex: String,
    val algorithm: ChecksumAlgorithm,
    val length: Array<Int>,
    val securityCodeLength: Array<Int>,
    @DrawableRes val iconResourceId: Int,
) {

    companion object {

        internal fun detect(card: String): VgsCardBrand {
            if (card.isBlank()) return CardType.UNKNOWN.toVgsCardBrand()
            CardType.entries.toTypedArray().except(CardType.UNKNOWN).forEach {
                val matcher = Pattern.compile(it.regex).matcher(card)
                while (matcher.find()) {
                    return it.toVgsCardBrand()
                }
            }
            return CardType.UNKNOWN.toVgsCardBrand()
        }
    }

    enum class ChecksumAlgorithm {

        LUHN,
        NONE
    }
}

internal fun CardType.toVgsCardBrand(): VgsCardBrand {
    return VgsCardBrand(
        name = this.name,
        mask = this.mask,
        regex = this.regex,
        algorithm = this.algorithm.toChecksumAlgorithm(),
        length = this.rangeNumber,
        securityCodeLength = this.rangeCVV,
        iconResourceId = this.resId
    )
}

internal fun LegacyChecksumAlgorithm.toChecksumAlgorithm(): VgsCardBrand.ChecksumAlgorithm {
    return when (this) {
        LegacyChecksumAlgorithm.LUHN, LegacyChecksumAlgorithm.ANY -> VgsCardBrand.ChecksumAlgorithm.LUHN
        LegacyChecksumAlgorithm.NONE -> VgsCardBrand.ChecksumAlgorithm.NONE
    }
}

internal fun VgsCardBrand.isValidCard(target: String): List<VgsTextFieldValidationResult> {
    val isLuhnValid  = if (this.algorithm == VgsCardBrand.ChecksumAlgorithm.LUHN) {
        VgsLuhnAlgorithmValidator().validate(target)
    } else {
        null
    }
    val isLengthValid = VgsTextLengthValidator(lengths = this.length).validate(target)
    return listOfNotNull(isLuhnValid, isLengthValid)
}