package com.verygoodsecurity.vgscollect.widget.compose.state.core

import com.verygoodsecurity.vgscollect.widget.compose.tokenization.VgsTokenizationConfig
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidationResult
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidator

internal const val ANALYTICS_UI = "native-compose"

/**
 * Common state contract shared by every VGS Compose field state
 * (card number, CVC, expiry, SSN, cardholder, generic text).
 *
 * Instances are immutable — every keystroke produces a new state object.
 * The raw text value is never exposed publicly; submit the state via
 * `VGSCollect.asyncSubmit(...)` and the SDK reads it internally.
 *
 * Obtain concrete states with the matching `rememberVgs…TextFieldState`
 * factory (e.g. `rememberVgsCardNumberTextFieldState`), then pass them to
 * the corresponding `Vgs…TextField` composable.
 *
 * @property fieldName JSON key the value is sent under in the submit payload.
 * @property validators custom validators applied to the value, or `null` to use
 *   the field's default validators.
 * @property isEmpty `true` when the field has no input.
 * @property contentLength current length of the entered value.
 * @property validationResult per-validator outcomes for the current value.
 * @property isValid `true` when every validator reports a valid result.
 * @property tokenizationConfig optional tokenization settings; when present,
 *   the value is tokenized on submit and the token is sent in its place.
 */
abstract class BaseFieldState(
    internal val text: String,
    val fieldName: String,
    val validators: List<VgsTextFieldValidator>?,
) {

    companion object {

        internal const val EMPTY = ""
    }

    val isEmpty: Boolean = text.isEmpty()

    val contentLength: Int = text.length

    val validationResult: List<VgsTextFieldValidationResult> by lazy { validate() }

    val isValid: Boolean by lazy { validationResult.all { it.isValid } }

    abstract val tokenizationConfig: VgsTokenizationConfig?

    internal abstract fun validate(): List<VgsTextFieldValidationResult>

    internal abstract fun getOutputText(): String

    internal abstract fun copy(text: String): BaseFieldState

    override fun toString(): String =
        "${this::class.simpleName}(fieldName=$fieldName, isValid=$isValid)"
}