package com.verygoodsecurity.vgscollect.widget.compose.validator.core

/**
 * Base type for all VGS Compose field validators.
 *
 * Pass a list of validators to a field state factory (e.g.
 * `rememberVgsCardNumberTextFieldState(validators = listOf(VgsRequiredFieldValidator()))`)
 * to override the field's default validation. Pass `emptyList()` to disable
 * validation entirely.
 *
 * Built-in implementations: [com.verygoodsecurity.vgscollect.widget.compose.validator.VgsRequiredFieldValidator],
 * [com.verygoodsecurity.vgscollect.widget.compose.validator.VgsTextLengthValidator],
 * [com.verygoodsecurity.vgscollect.widget.compose.validator.VgsRegexValidator],
 * [com.verygoodsecurity.vgscollect.widget.compose.validator.VgsLuhnAlgorithmValidator],
 * [com.verygoodsecurity.vgscollect.widget.compose.validator.VgsMinMaxDateValidator].
 *
 * @property errorMsg the message reported when validation fails.
 */
abstract class VgsTextFieldValidator {

    abstract val errorMsg: String

    internal abstract fun validate(text: String): VgsTextFieldValidationResult
}