package com.verygoodsecurity.vgscollect.widget.compose.core

import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidationResult

abstract class BaseFieldState(
    internal val text: String,
    val fieldName: String,
) {

    companion object {

        internal const val EMPTY = ""
    }

    val isEmpty: Boolean = text.isEmpty()

    val contentLength: Int = text.length

    abstract fun isValid(): Boolean

    abstract fun validate(): List<VgsTextFieldValidationResult>

    internal abstract fun getOutputText(): String
}