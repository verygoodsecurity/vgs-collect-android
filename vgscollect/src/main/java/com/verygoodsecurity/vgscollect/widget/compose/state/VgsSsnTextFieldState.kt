package com.verygoodsecurity.vgscollect.widget.compose.state

import com.verygoodsecurity.vgscollect.widget.compose.state.core.BaseFieldState
import com.verygoodsecurity.vgscollect.widget.compose.tokenization.VgsSsnTokenizationConfig
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsRegexValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsRequiredFieldValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsTextLengthValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidationResult
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidator
import kotlin.math.min

class VgsSsnTextFieldState(
    text: String,
    fieldName: String,
    validators: List<VgsTextFieldValidator>?,
    override val tokenizationConfig: VgsSsnTokenizationConfig? = null,
) : BaseFieldState(text, fieldName, validators, tokenizationConfig) {

    private companion object {

        const val DEFAULT_MASK = "###-##-####"
        const val DEFAULT_LENGTH = 9
        const val DEFAULT_REGEX = "^(?!(000|666|9))\\d{3}(?!(00))\\d{2}(?!(0000))\\d{4}$"
    }

    val mask: String = DEFAULT_MASK

    constructor(
        fieldName: String,
        validators: List<VgsTextFieldValidator>? = null,
        tokenizationConfig: VgsSsnTokenizationConfig? = null,
    ) : this(EMPTY, fieldName, validators, tokenizationConfig)

    override fun validate(): List<VgsTextFieldValidationResult> {
        return (validators ?: getDefaultValidators()).map { it.validate(text) }
    }

    override fun getOutputText(): String = text

    override fun copy(text: String): VgsSsnTextFieldState {
        return VgsSsnTextFieldState(
            text = normalizeText(text),
            fieldName = fieldName,
            validators = validators,
            tokenizationConfig = tokenizationConfig,
        )
    }

    private fun getDefaultValidators(): List<VgsTextFieldValidator> {
        return listOf(
            VgsRequiredFieldValidator(),
            VgsTextLengthValidator(arrayOf(DEFAULT_LENGTH)),
            VgsRegexValidator(DEFAULT_REGEX)
        )
    }

    private fun normalizeText(text: String): String {
        val digits = text.filter { it.isDigit() }
        val length = digits.length
        return digits.substring(0, min(length, DEFAULT_LENGTH))
    }
}
