package com.verygoodsecurity.vgscollect.widget.compose.state

import com.verygoodsecurity.vgscollect.widget.compose.card.VgsCardBrand
import com.verygoodsecurity.vgscollect.widget.compose.card.getSecurityCodeValidators
import com.verygoodsecurity.vgscollect.widget.compose.state.core.BaseFieldState
import com.verygoodsecurity.vgscollect.widget.compose.tokenization.VgsCvcTokenizationConfig
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsRequiredFieldValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidationResult
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidator
import kotlin.math.min

class VgsCvcTextFieldState internal constructor(
    text: String,
    fieldName: String,
    validators: List<VgsTextFieldValidator>?,
    val isCardBrandValidationEnabled: Boolean,
    val cardBrand: VgsCardBrand,
    override val tokenizationConfig: VgsCvcTokenizationConfig? = null,
) : BaseFieldState(text, fieldName, validators, tokenizationConfig) {

    constructor(
        fieldName: String,
        validators: List<VgsTextFieldValidator>? = null,
        isCardBrandValidationEnabled: Boolean = true,
        tokenizationConfig: VgsCvcTokenizationConfig? = null,
    ) : this(
        EMPTY,
        fieldName,
        validators,
        isCardBrandValidationEnabled,
        VgsCardBrand.UNKNOWN,
        tokenizationConfig,
    )

    override fun validate(): List<VgsTextFieldValidationResult> {
        val completeValidators = with(validators ?: listOf(VgsRequiredFieldValidator())) {
            if (isCardBrandValidationEnabled) {
                this + cardBrand.getSecurityCodeValidators()
            } else {
                this
            }
        }
        return completeValidators.map { it.validate(text) }
    }

    override fun getOutputText(): String = text

    override fun copy(text: String): VgsCvcTextFieldState {
        return VgsCvcTextFieldState(
            text = normalizeText(text, cardBrand),
            fieldName = fieldName,
            validators = validators,
            isCardBrandValidationEnabled = isCardBrandValidationEnabled,
            cardBrand = cardBrand,
            tokenizationConfig = tokenizationConfig,
        )
    }

    /**
     * Updates the security code field state when a new card brand is detected
     * in the card number field.
     *
     * ### Example usage:
     *
     * ```
     * LaunchedEffect(cardNumberFieldState.cardBrand) {
     *     cvcState = cvcState.withCardBrand(cardNumberFieldState.cardBrand)
     * }
     * ```
     *
     * @param cardBrand The card brand detected in [VgsCardNumberTextFieldState]
     */
    fun withCardBrand(cardBrand: VgsCardBrand) = VgsCvcTextFieldState(
        text = normalizeText(text, cardBrand),
        fieldName = fieldName,
        validators = validators,
        isCardBrandValidationEnabled = isCardBrandValidationEnabled,
        cardBrand = cardBrand,
        tokenizationConfig = tokenizationConfig,
    )

    private fun normalizeText(text: String, cardBrand: VgsCardBrand): String {
        val digits = text.filter { it.isDigit() }
        val length = digits.length
        return digits.substring(0, min(length, cardBrand.securityCodeLength.maxOrNull() ?: length))
    }
}
