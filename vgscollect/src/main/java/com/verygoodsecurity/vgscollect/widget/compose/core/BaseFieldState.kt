package com.verygoodsecurity.vgscollect.widget.compose.core

import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidationResult
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidator

abstract class BaseFieldState(
    internal val text: String,
    val fieldName: String,
    val validators: List<VgsTextFieldValidator>
) {

    companion object {

        internal const val EMPTY = ""
    }

    val validationResults: List<VgsTextFieldValidationResult> = validate()

    val isValid: Boolean = validationResults.all { it.isValid }

    val isEmpty: Boolean = text.isEmpty()

    val contentLength: Int = text.length

    protected open fun validate(): List<VgsTextFieldValidationResult> {
        return validators.map { it.validate(text) }
    }
}