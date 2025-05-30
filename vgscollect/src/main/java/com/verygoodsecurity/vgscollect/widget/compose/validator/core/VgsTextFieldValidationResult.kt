package com.verygoodsecurity.vgscollect.widget.compose.validator.core

abstract class VgsTextFieldValidationResult {

    abstract val isValid: Boolean

    abstract val errorMsg: String?
}