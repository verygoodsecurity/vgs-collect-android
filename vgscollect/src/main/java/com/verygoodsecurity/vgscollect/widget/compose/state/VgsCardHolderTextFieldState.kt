package com.verygoodsecurity.vgscollect.widget.compose.state

import com.verygoodsecurity.vgscollect.widget.compose.state.core.BaseFieldState
import com.verygoodsecurity.vgscollect.widget.compose.tokenization.VgsCardHolderTokenizationConfig
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsRegexValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsRequiredFieldValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidationResult
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidator

class VgsCardHolderTextFieldState(
    text: String,
    fieldName: String,
    validators: List<VgsTextFieldValidator>?,
    override val tokenizationConfig: VgsCardHolderTokenizationConfig? = null,
) : BaseFieldState(text, fieldName, validators, tokenizationConfig) {

    private companion object {

        const val VALIDATION_REGEX = "^[a-zA-Z0-9 ,\\'.-]+$"
    }

    constructor(
        fieldName: String,
        validators: List<VgsTextFieldValidator>? = null,
        tokenizationConfig: VgsCardHolderTokenizationConfig? = null,
    ) : this(EMPTY, fieldName, validators, tokenizationConfig)

    override fun validate(): List<VgsTextFieldValidationResult> {
        return (validators ?: listOf(
            VgsRequiredFieldValidator(),
            VgsRegexValidator(VALIDATION_REGEX)
        )).map { it.validate(text) }
    }

    override fun getOutputText(): String = text

    override fun copy(text: String): VgsCardHolderTextFieldState {
        return VgsCardHolderTextFieldState(
            text = text,
            fieldName = fieldName,
            validators = validators,
            tokenizationConfig = tokenizationConfig,
        )
    }
}
