package com.verygoodsecurity.vgscollect.widget.compose.validator.core

abstract class VgsTextFieldValidator {

    abstract val errorMsg: String?

    internal abstract fun validate(text: String): VgsTextFieldValidationResult
}