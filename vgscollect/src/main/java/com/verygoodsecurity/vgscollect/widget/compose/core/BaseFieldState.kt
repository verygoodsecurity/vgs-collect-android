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

    val validationResults: List<VgsTextFieldValidationResult>

    val isValid: Boolean

    val isEmpty: Boolean

    val contentLength: Int

    init {
        validationResults = validate()
        isValid = validationResults.all { it.isValid }
        isEmpty = text.isEmpty()
        contentLength = text.length
    }

    private fun validate(): List<VgsTextFieldValidationResult> {
        return validators.map { it.validate(text) }
    }
}