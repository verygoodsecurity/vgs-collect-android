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

    val isEmpty: Boolean = text.isEmpty()

    val contentLength: Int = text.length

    open fun validate(): List<VgsTextFieldValidationResult> {
        return validators.map { it.validate(text) }
    }

    open fun isValid(): Boolean {
        return validate().all { it.isValid }
    }
}