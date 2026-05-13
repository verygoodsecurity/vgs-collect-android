package com.verygoodsecurity.vgscollect.widget.compose.state.core

import com.verygoodsecurity.vgscollect.widget.compose.tokenization.VgsTokenizationConfig
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidationResult
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidator

abstract class BaseFieldState(
    internal val text: String,
    val fieldName: String,
    val validators: List<VgsTextFieldValidator>?,
    open val tokenizationConfig: VgsTokenizationConfig? = null,
) {

    companion object {

        internal const val EMPTY = ""
    }

    val isEmpty: Boolean = text.isEmpty()

    val contentLength: Int = text.length

    val validationResult: List<VgsTextFieldValidationResult> by lazy { validate() }

    val isValid: Boolean by lazy { validationResult.all { it.isValid } }

    internal abstract fun validate(): List<VgsTextFieldValidationResult>

    internal abstract fun getOutputText(): String

    internal abstract fun copy(text: String): BaseFieldState

    override fun toString(): String =
        "${this::class.simpleName}(fieldName=$fieldName, isValid=$isValid)"
}