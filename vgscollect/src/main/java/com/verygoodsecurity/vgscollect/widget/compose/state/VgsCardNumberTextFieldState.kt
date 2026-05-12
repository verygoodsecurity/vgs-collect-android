package com.verygoodsecurity.vgscollect.widget.compose.state

import com.verygoodsecurity.vgscollect.core.model.state.allowed8DigitBIN
import com.verygoodsecurity.vgscollect.widget.compose.card.VgsCardBrand
import com.verygoodsecurity.vgscollect.widget.compose.card.getCardNumberValidators
import com.verygoodsecurity.vgscollect.widget.compose.state.core.BaseFieldState
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsRequiredFieldValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidationResult
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidator
import kotlin.math.max
import kotlin.math.min

class VgsCardNumberTextFieldState internal constructor(
    text: String,
    fieldName: String,
    validators: List<VgsTextFieldValidator>?,
    val isCardBrandValidationEnabled: Boolean,
    val supportedCardBrands: List<VgsCardBrand>,
    val cardBrand: VgsCardBrand,
) : BaseFieldState(text, fieldName, validators) {

    val bin: String? = generateBin()

    val last4: String? = generateLast4()

    constructor(
        fieldName: String,
        validators: List<VgsTextFieldValidator>? = null,
        isCardBrandValidationEnabled: Boolean = true,
        supportedCardBrands: List<VgsCardBrand> = VgsCardBrand.DEFAULT
    ) : this(
        EMPTY,
        fieldName,
        validators,
        isCardBrandValidationEnabled,
        supportedCardBrands,
        VgsCardBrand.UNKNOWN,
    )

    override fun validate(): List<VgsTextFieldValidationResult> {
        val completeValidators = with(validators ?: listOf(VgsRequiredFieldValidator())) {
            if (isCardBrandValidationEnabled) {
                this + cardBrand.getCardNumberValidators()
            } else {
                this
            }
        }
        return completeValidators.map { it.validate(text) }
    }

    override fun getOutputText(): String = text

    override fun copy(text: String): VgsCardNumberTextFieldState {
        val cardBrand = VgsCardBrand.detect(text, supportedCardBrands)
        return VgsCardNumberTextFieldState(
            text = normalizeText(text, cardBrand),
            fieldName = fieldName,
            validators = validators,
            isCardBrandValidationEnabled = isCardBrandValidationEnabled,
            supportedCardBrands = supportedCardBrands,
            cardBrand = cardBrand,
        )
    }

    private fun normalizeText(text: String, cardBrand: VgsCardBrand): String {
        val digits = text.filter(Char::isDigit)
        val length = digits.length
        return digits.substring(0, min(length, cardBrand.length.maxOrNull() ?: length))
    }

    private fun generateBin(): String? {
        val length = if (allowed8DigitBIN(cardBrand.name, text.length)) 8 else 6
        return if (isValid) text.substring(0, min(text.length, length)) else null
    }

    private fun generateLast4(): String? {
        return if (isValid) text.substring(max(0, text.length - 4), text.length) else null
    }
}