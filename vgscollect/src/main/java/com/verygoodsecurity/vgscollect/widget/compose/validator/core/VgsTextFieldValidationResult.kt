package com.verygoodsecurity.vgscollect.widget.compose.validator.core

/**
 * Outcome of a single validator run.
 *
 * Read [com.verygoodsecurity.vgscollect.widget.compose.state.core.BaseFieldState.validationResult]
 * to inspect per-validator results, or [com.verygoodsecurity.vgscollect.widget.compose.state.core.BaseFieldState.isValid]
 * for the aggregate outcome.
 *
 * @property isValid `true` when the value passed this validator's check.
 * @property errorMsg the failure message when [isValid] is `false`, otherwise `null`.
 */
abstract class VgsTextFieldValidationResult {

    abstract val isValid: Boolean

    abstract val errorMsg: String?

    override fun toString(): String =
        "${this::class.simpleName}(isValid=$isValid, errorMsg=$errorMsg)"
}