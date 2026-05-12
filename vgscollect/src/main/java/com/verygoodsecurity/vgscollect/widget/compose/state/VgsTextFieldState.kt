package com.verygoodsecurity.vgscollect.widget.compose.state

import com.verygoodsecurity.vgscollect.widget.compose.state.core.BaseFieldState
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsRequiredFieldValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidationResult
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidator

class VgsTextFieldState(
    text: String,
    fieldName: String,
    validators: List<VgsTextFieldValidator>?
) : BaseFieldState(text, fieldName, validators) {

    constructor(
        fieldName: String,
        validators: List<VgsTextFieldValidator>? = null
    ) : this(
        EMPTY,
        fieldName,
        validators
    )

    override fun validate(): List<VgsTextFieldValidationResult> {
        return (validators ?: listOf(VgsRequiredFieldValidator())).map { it.validate(text) }
    }

    override fun getOutputText(): String = text

    override fun copy(text: String): VgsTextFieldState {
        return VgsTextFieldState(text = text, fieldName = fieldName, validators = validators)
    }
}
